package com.longzx.blog.controller;

import com.longzx.blog.service.CommentService;
import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.params.CommentParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    CommentService commentService;

    /**
     * 获取文章的评论
     * @param id
     * @return
     */
    @GetMapping("/article/{id}")
    public Result commentsOfArticle(@PathVariable("id") Long id){
        return commentService.commentsArticleById(id);
    }

    /**
     * 评论文章，这个请求需要用户登入后才可以进行请求，因此加入登录拦截器
     */
    @PostMapping("/create/change")
    public Result commentToArticle(@RequestBody CommentParams commentParams){
        return commentService.commentToArticle(commentParams);
    }

}
