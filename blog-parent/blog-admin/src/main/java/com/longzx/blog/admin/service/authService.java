package com.longzx.blog.admin.service;

import com.longzx.blog.admin.dao.pojo.Admin;
import com.longzx.blog.admin.dao.pojo.Permission;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
@Slf4j
public class authService {

    @Autowired
    AdminService adminService;

    public boolean auth(HttpServletRequest request, Authentication authentication){
        /**
         * 权限认证步骤：
         * 1.从request中获取当前的请求路径
         * 2.从authentication中获取当前用户信息
         * 3.通过username从admin表中查是否有这个用户，没有的话直接返回false
         * 4.如果这个用户是超级用户，则直接返回true,表示该用户拥有访问一切url的权限
         * 5.如果不是超级用户，则通过用户id从admin_permission表中查该用户的权限id
         * 6.得到该用户的权限id后在permission表中查该权限id对应的path,即该权限可以访问的url
         * 7.如果该权限id对应的url与用户请求的url不一致，则该用户没有权限访问
         */
        //请求路径
        String requestURI = request.getRequestURI();
        log.info("request url:{}", requestURI);
        //true代表放行 false 代表拦截
        Object principal = authentication.getPrincipal();
        if (principal == null || "anonymousUser".equals(principal)){
            //未登录or匿名用户，直接返回false
            return false;
        }
        UserDetails userDetails = (UserDetails) principal;
        String username = userDetails.getUsername();
        Admin admin = adminService.findUserByName(username);
        if (admin == null){
            return false;
        }
        if (admin.getId() == 1){
            //认为是超级管理员
            return true;
        }
        List<Permission> permissions = adminService.findPermissionsByAdminId(admin.getId());
        //将url后面拼接的请求参数去除
        requestURI = StringUtils.split(requestURI,'?')[0];
        //匹配路径
        for (Permission permission : permissions) {
            if (requestURI.equals(permission.getPath())){
                log.info("权限通过");
                return true;
            }
        }
        return false;
    }
}
