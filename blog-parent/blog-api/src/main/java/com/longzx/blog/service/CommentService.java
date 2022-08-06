package com.longzx.blog.service;

import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.params.CommentParams;

public interface CommentService {

    /**
     * 根据文章Id查询文章所有的评论
     * @param id
     * @return
     */
    Result commentsArticleById(Long id);

    /**
     * 用户评论文章
     * @param commentParams
     * @return
     */
    Result commentToArticle(CommentParams commentParams);
}
