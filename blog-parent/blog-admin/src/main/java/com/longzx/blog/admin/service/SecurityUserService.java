package com.longzx.blog.admin.service;

import com.longzx.blog.admin.dao.pojo.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class SecurityUserService implements UserDetailsService {

    @Autowired
    AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * 1.登录的时候会把username传递到这里
         * 2.这里通过username去查admin表，如果username存在，将password传递给spring security模块
         * 3.不存在就直接返回null
         */

        Admin user = adminService.findUserByName(username);
        if (user == null){
            throw new UsernameNotFoundException("用户名不存在");
        }

        UserDetails userDetails = new User(username, user.getPassword(), new ArrayList<>());

        return userDetails;
    }
}
