package com.longzx.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longzx.blog.dao.pojo.Category;
import com.longzx.blog.vo.CategoryVo;
import org.springframework.stereotype.Component;

@Component
public interface CategoryMapper extends BaseMapper<Category> {
}
