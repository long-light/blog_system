package com.longzx.blog.service.impl;

import com.longzx.blog.dao.mapper.CategoryMapper;
import com.longzx.blog.dao.pojo.Category;
import com.longzx.blog.service.CategoryService;
import com.longzx.blog.vo.CategoryVo;
import com.longzx.blog.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public CategoryVo findCategoryById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        CategoryVo categoryVo = new CategoryVo();
        categoryVo.setId(category.getId());
        categoryVo.setAvatar(category.getAvatar());
        categoryVo.setCategoryName(category.getCategoryName());

        return categoryVo;
    }

    /**
     * 找到所有的文章类别
     * @return
     */
    @Override
    public Result findAll() {
        List<Category> categories = categoryMapper.selectList(null);
        //将category转换为categoryVo
        List<CategoryVo> categoryVoList = copyList(categories);
        return Result.success(categoryVoList);
    }

    /**
     * 找到所有类别的详细信息，包括id, avatar, categoryName, description
     * @return
     */
    @Override
    public Result categoryDetail() {
        List<Category> categories = categoryMapper.selectList(null);
        return Result.success(copyList(categories));
    }

    @Override
    public Result findCategoryDetailById(Long id) {
        Category category = categoryMapper.selectById(id);
        return Result.success(copy(category));
    }

    private List<CategoryVo> copyList(List<Category> categories) {
        ArrayList<CategoryVo> categoryVos = new ArrayList<>();
        for (Category category : categories) {
            categoryVos.add(copy(category));
        }
        return categoryVos;
    }

    private CategoryVo copy(Category category) {
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category, categoryVo);
        categoryVo.setId(category.getId());
        return categoryVo;
    }

}
