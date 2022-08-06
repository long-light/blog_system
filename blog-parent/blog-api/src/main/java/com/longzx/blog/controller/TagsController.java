package com.longzx.blog.controller;

import com.longzx.blog.service.TagService;
import com.longzx.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
public class TagsController {

    @Autowired
    TagService tagService;

    @GetMapping
    public Result findAll(){
        return tagService.findAll();
    }

    @GetMapping("/hot")
    public Result hotTags(){
        //最热标签展示数量
        int hotTagsLimits = 5;
        return tagService.hotTags(hotTagsLimits);
    }

    /**
     * 查询tag详细信息
     * @return
     */
    @GetMapping("/detail")
    public Result tagsDetail(){
        return tagService.findTagsDetail();
    }

    @GetMapping("/detail/{id}")
    public Result findTagDetailById(@PathVariable("id") Long id){
        return tagService.findTagDetailById(id);
    }
}
