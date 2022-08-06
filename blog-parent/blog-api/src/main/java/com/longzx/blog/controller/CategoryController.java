package com.longzx.blog.controller;

import com.longzx.blog.service.CategoryService;
import com.longzx.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorys")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /**
     * 查询所有类别
     * @return
     */
    @GetMapping
    public Result categories(){
        return categoryService.findAll();
    }

    /**
     * 查询所有类别的详细信息
     * @return
     */
    @GetMapping("/detail")
    public Result categoryDetail(){
        return categoryService.categoryDetail();
    }

    /**
     * 根据id查询类别的详细信息
     * @param id
     * @return
     */
    @GetMapping("/detail/{id}")
    public Result findCategoryDetailById(@PathVariable("id") Long id){
        return categoryService.findCategoryDetailById(id);
    }
}
