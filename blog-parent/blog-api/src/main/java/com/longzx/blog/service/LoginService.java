package com.longzx.blog.service;


import com.longzx.blog.dao.pojo.SysUser;
import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.params.LoginParams;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LoginService {

    /**
     * 登入验证
     * @param loginParams
     * @return
     */
    Result login(LoginParams loginParams);

    /**
     * 通过token来获取用户信息
     * @param token
     * @return
     */
    SysUser getUserByToken(String token);

    /**
     * 登出服务
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 新用户注册
     * @param loginParams
     * @return
     */
    Result register(LoginParams loginParams);
}
