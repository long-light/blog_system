package com.longzx.blog.controller;

import com.longzx.blog.dao.mapper.SysUserMapper;
import com.longzx.blog.service.SysUserService;
import com.longzx.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    SysUserService sysUserService;

    @RequestMapping("/currentUser")
    public Result currentUser(@RequestHeader("Authorization") String token){
        return sysUserService.findUserByToken(token);
    }

}
