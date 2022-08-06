package com.longzx.blog.admin.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Permission {

    //在实体类中uid属性上通过@TableId将其标识为主键，即可成功执行SQL语句
    //使用数据库的自增策略,而不使用雪花算法生成ID
    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private String path;

    private String description;

}
