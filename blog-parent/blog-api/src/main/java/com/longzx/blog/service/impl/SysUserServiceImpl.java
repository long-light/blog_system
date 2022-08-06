package com.longzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longzx.blog.dao.mapper.SysUserMapper;
import com.longzx.blog.dao.pojo.SysUser;
import com.longzx.blog.service.LoginService;
import com.longzx.blog.service.SysUserService;
import com.longzx.blog.vo.ErrorCode;
import com.longzx.blog.vo.LonginUserVo;
import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    LoginService loginService;

    @Override
    public SysUser findUserById(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setNickname("longzx");
        }
        return sysUser;
    }

    /**
     * 用于验证登录的用户名和密码
     * @param account
     * @param password
     * @return
     */
    @Override
    public SysUser findUserByNameAndPassword(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        //account id 头像 名称
        queryWrapper.select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getNickname);
        //增加查询效率，只查询一条
        queryWrapper.last("limit 1"); //使用selectOne时候，为防止出错，最好加上limit
        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 根据token来获取用户信息
     * @param token
     * @return
     */
    @Override
    public Result findUserByToken(String token) {
        /**
         * 1、token合法性校验
         * 是否为空，解析是否成功，redis是否存在
         * 2、如果校验失败，返回错误
         * 3、如果成功，返回对应结果 LoginUserVo
         */
        //去loginService中去校验token
        SysUser sysUser = loginService.getUserByToken(token);
        if (sysUser == null){
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        //通过LoginUserVo来向前端返回查询的数据
        LonginUserVo longinUserVo = new LonginUserVo();
        longinUserVo.setId(sysUser.getId());
        longinUserVo.setAccount(sysUser.getAccount());
        longinUserVo.setNickname(sysUser.getNickname());
        longinUserVo.setAvatar(sysUser.getAvatar());

        return Result.success(longinUserVo);
    }

    /**
     * 根据account在数据库中找到指定的user信息
     * @param account
     * @return
     */
    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount, account);
        queryWrapper.last("limit 1");

        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 将新用户存储在数据库中
     * @param sysUser
     */
    @Override
    public void save(SysUser sysUser) {
        sysUserMapper.insert(sysUser);
    }

    /**
     * 根据id查找yuser信息，并且封装在userVo对象中
     * @param authorId
     * @return
     */
    @Override
    public UserVo findUserVoById(Long authorId) {
        SysUser sysUser = sysUserMapper.selectById(authorId);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setId(110L);
            sysUser.setNickname("longzx");
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(sysUser, userVo);
        return userVo;
    }
}
