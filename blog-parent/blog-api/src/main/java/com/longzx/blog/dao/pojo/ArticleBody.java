package com.longzx.blog.dao.pojo;

import lombok.Data;

/**
 * 映射ms_article_body表
 */
@Data
public class ArticleBody {

    private Long id;
    //md格式的内容
    private String content;
    //html格式的内容
    private String contentHtml;

    private Long articleId;
}


