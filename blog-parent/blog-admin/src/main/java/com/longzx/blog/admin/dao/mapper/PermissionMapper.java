package com.longzx.blog.admin.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longzx.blog.admin.dao.pojo.Permission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PermissionMapper extends BaseMapper<Permission> {

    List<Permission> findPermissionsByAdminId(Long id);
}
