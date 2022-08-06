package com.longzx.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.longzx.blog.dao.pojo.SysUser;
import com.longzx.blog.service.LoginService;
import com.longzx.blog.service.SysUserService;
import com.longzx.blog.utils.JWTUtils;
import com.longzx.blog.utils.userUtils;
import com.longzx.blog.vo.ErrorCode;
import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.params.LoginParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    //登录需要使用SysUserService去查询ms_sys_user表
    @Autowired
    SysUserService sysUserService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;

    //加密盐用于加密
    private static final String slat = "longzx!@#";

    /**
     * 从ms_sys_user表中查询是否有loginParams中的账号和密码
     * @param loginParams
     * @return
     */
    @Override
    public Result login(LoginParams loginParams) {
        //1. 检查参数是否合法
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        if (account.equals("") || password.equals(""))
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        //2. 根据用户名和密码去user表中查询，是否存在
        password = DigestUtils.md5Hex(password + slat);
        SysUser sysUser = sysUserService.findUserByNameAndPassword(account, password);
        //3. 如果不存在，登录失败
        if (sysUser == null) return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        //4. 如果存在，就使用JWT生成一个token返回给前端
        String token = JWTUtils.createToken(sysUser.getId());
        //5. token放入redis中：token:user信息并且设置过期时间 -->根据token快速返回user信息，减少对表的查询
        //set中参数：key, value, 过期时间：1 * TimeUint.DAYS
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser), 1, TimeUnit.DAYS);

        return Result.success(token);
    }

    /**
     * 根据用户信息来从redis中获取token
     * @param token
     * @return
     */
    @Override
    public SysUser getUserByToken(String token) {
        //判断token是否为空
        if (StringUtils.isBlank(token)) return null;
        //token不为空，进行token解密
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null) return null;

        //通过上述步骤验证了token的合法性，然后从redis中取出user信息
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        //如果userJson是空，表示redis中存储的user信息过期了或者由于用户登出而从redis中清除，需要用户重新登录
        if (StringUtils.isBlank(userJson)) return null;

        //将Json数据解析成SysUser
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

    /**
     * 登出服务，将token从redis中删除，然后将给返回给前端一个为null的响应体
     * @param token
     * @return
     */
    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }

    @Override
    public Result register(LoginParams loginParams) {
        //1. 将所有数据从loginParams取出
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        String nickname = loginParams.getNickname();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }

        //从数据库中看是否存在该用户
        SysUser sysUser = sysUserService.findUserByAccount(account);
        if (sysUser != null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(), ErrorCode.ACCOUNT_EXIST.getMsg());
        }
        //将信息注入到sysUser中
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/user/user_3.png");
        sysUser.setMobilePhoneNumber(userUtils.getTel());
        sysUser.setAdmin(0); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt(slat);
        sysUser.setStatus("0");
        sysUser.setEmail(account + "@blog.com");

        //将信息存在在SQL数据库中
        sysUserService.save(sysUser);

        //返回一个token给客户端
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser), 1, TimeUnit.DAYS);
        return Result.success(token);
    }

    public static void main(String[] args) {
        //生成密码
        String[] names = {"admin", "lisi"};
        for (String name : names) {
            System.out.println(DigestUtils.md5Hex(name+slat));
        }
    }
}
