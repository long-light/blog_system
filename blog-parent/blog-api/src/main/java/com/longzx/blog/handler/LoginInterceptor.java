package com.longzx.blog.handler;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.longzx.blog.dao.pojo.SysUser;
import com.longzx.blog.service.LoginService;
import com.longzx.blog.utils.UserThreadLocal;
import com.longzx.blog.vo.ErrorCode;
import com.longzx.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /**
         * 1. 首先需要判断前端是否请求的是controller方法，还是请求classpath:static下的资源,只对前端请求控制器方法时才进行拦截
         * 2. 判断token是否为空，在request的header中；如果为空则表示未登录
         * 3. 判断token是否合法,如果合法就返回该token对应的user信息，使用loginservier.getUserByToken();
         * 4. 如果认证成功，拦截器放行该请求
         */

        if (!(handler instanceof HandlerMethod))
            //ResourceHttpRequestHandler是对资源的请求
            return true;

        String token = request.getHeader("Authorization");
        if (StringUtils.isBlank(token)){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            //设置返回内容形式为json
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        SysUser sysUser = loginService.getUserByToken(token);
        if (sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), "未登录");
            //设置返回内容形式为json
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().print(JSON.toJSONString(result));
            return false;
        }

        //登录验证成功之后，使用threadLocal(本地线程的局部变量)来保存用户信息，这样在同一个请求(一个请求对应一个线程)的任何方法中，都可以获取到用户信息
        //开始我们将token:sysUser以键值对的形式放在redis中，这样每次都需要先获取客户端的request.header里面的Authorization的参数值(即token)
        //来从redis中获取sysUser,导致在任何需要user信息的方法中都需要先获取token, 这导致需要先获取request。
        //当然，我们也可以将用户信息存放在服务端的session中，然而获取session依旧需要一个request
        UserThreadLocal.put(sysUser);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //请求处理完成后，需要将存放在ThreadLocal中的sysUser删除，
        //否则客户端发来新请求时，会重新创建一个线程，将sysUser信息存放到新线程的ThreadLocal中
        //上一个线程存放的user信息无法再获取，就会造成内存泄露
        UserThreadLocal.remove();
    }
}
