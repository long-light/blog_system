package com.longzx.blog.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longzx.blog.admin.dao.mapper.AdminMapper;
import com.longzx.blog.admin.dao.mapper.PermissionMapper;
import com.longzx.blog.admin.dao.pojo.Admin;
import com.longzx.blog.admin.dao.pojo.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    AdminMapper adminMapper;
    @Autowired
    PermissionMapper permissionMapper;

    public Admin findUserByName(String username) {
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username);
        queryWrapper.last("limit 1");

        return adminMapper.selectOne(queryWrapper);
    }

    /**
     * 1.根据adminId先从ms_admin_permission表中查出用户对应的权限permission_id: admin_id -> permission_id是一对多
     * 2.然后根据permission_id在ms_permission中查出对应的权限，即可以访问的url
     * @param id
     * @return
     */
    public List<Permission> findPermissionsByAdminId(Long id) {
        return permissionMapper.findPermissionsByAdminId(id);
    }
}
