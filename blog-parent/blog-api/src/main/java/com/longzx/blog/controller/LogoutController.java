package com.longzx.blog.controller;

import com.longzx.blog.service.LoginService;
import com.longzx.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logout")
public class LogoutController {

    @Autowired
    LoginService loginService;

    /**
     * 登出服务就是将当前用户对应的token信息从redis中删除
     * @param token
     * @return
     */
    @GetMapping
    public Result logout(@RequestHeader("Authorization") String token){
        return loginService.logout(token);
    }
}
