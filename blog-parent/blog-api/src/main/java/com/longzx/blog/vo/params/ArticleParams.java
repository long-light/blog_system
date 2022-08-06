package com.longzx.blog.vo.params;

import com.longzx.blog.vo.CategoryVo;
import com.longzx.blog.vo.TagVo;
import lombok.Data;

import java.util.List;


@Data
public class ArticleParams {

    private Long id;

    //ArticleBodyParam：content是文章的md格式，content_html是文章的html格式
    private ArticleBodyParam body;

    //{id: 2, avatar: “/category/back.png”, categoryName: “后端”}
    private CategoryVo category;

    private String summary;

    //[{id: 5}, {id: 6}]
    private List<TagVo> tags;

    private String title;

    private String search;
}
