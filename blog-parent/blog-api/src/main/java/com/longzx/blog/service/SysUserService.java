package com.longzx.blog.service;

import com.longzx.blog.dao.pojo.SysUser;
import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.UserVo;

public interface SysUserService {

    SysUser findUserById(Long userId);

    SysUser findUserByNameAndPassword(String account, String password);

    Result findUserByToken(String token);

    SysUser findUserByAccount(String account);

    void save(SysUser sysUser);

    UserVo findUserVoById(Long authorId);
}
