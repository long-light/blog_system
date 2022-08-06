package com.longzx.blog.service;

import com.longzx.blog.vo.CategoryVo;
import com.longzx.blog.vo.Result;

public interface CategoryService {

    CategoryVo findCategoryById(Long categoryId);

    Result findAll();

    Result categoryDetail();

    Result findCategoryDetailById(Long id);
}
