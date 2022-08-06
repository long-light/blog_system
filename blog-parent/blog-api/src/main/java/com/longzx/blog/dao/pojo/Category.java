package com.longzx.blog.dao.pojo;

import lombok.Data;

/**
 * 映射ms_category
 */
@Data
public class Category {

    private Long id;
    //类别图标
    private String avatar;
    //类别名称
    private String categoryName;
    //类别描述
    private String description;
}
