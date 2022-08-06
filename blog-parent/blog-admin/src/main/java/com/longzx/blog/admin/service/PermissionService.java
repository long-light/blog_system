package com.longzx.blog.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longzx.blog.admin.dao.mapper.PermissionMapper;
import com.longzx.blog.admin.dao.pojo.Permission;
import com.longzx.blog.admin.vo.PageVo;
import com.longzx.blog.admin.vo.Result;
import com.longzx.blog.admin.vo.param.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    PermissionMapper permissionMapper;

    /**
     * 查询权限列表
     * @param pageParam
     * @return
     */
    public Result listPermission(PageParam pageParam) {
        Page<Permission> permissionPage = new Page<>(pageParam.getCurrentPage(), pageParam.getPageSize());
        LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper<>();
        if (pageParam.getQueryString() != null){
            queryWrapper.eq(Permission::getName, pageParam.getQueryString());
        }
        Page<Permission> permissionList = permissionMapper.selectPage(permissionPage, queryWrapper);
        PageVo<Permission> permissionPageVo = new PageVo<>();
        permissionPageVo.setList(permissionList.getRecords());
        permissionPageVo.setTotal(permissionList.getTotal());

        return Result.success(permissionPageVo);
    }

    public Result add(Permission permission) {
        permissionMapper.insert(permission);
        return Result.success(permission.getId());
    }

    public Result update(Permission permission) {
        permissionMapper.updateById(permission);
        return Result.success(null);
    }

    public Result delete(Long id) {
        permissionMapper.deleteById(id);
        return Result.success(null);
    }
}
