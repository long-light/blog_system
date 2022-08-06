package com.longzx.blog.service;

import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.TagVo;

import java.util.List;

public interface TagService {
    /**
     * 根据文章ID查询文章的标签
     * @param articleId
     * @return
     */
    List<TagVo> findArticleTagById(Long articleId);

    /**
     * 查询最热的文章标签，即统计ms_article_tag下面同一个tag_id下文章数量
     * @param hotTagsLimits
     * @return
     */
    Result hotTags(int hotTagsLimits);

    /**
     * 查询所有标签
     * @return
     */
    Result findAll();

    /**
     * 查询标签详细信息
     * @return
     */
    Result findTagsDetail();

    /**
     * 根据tagId查询tag的详细信息
     * @param id
     * @return
     */
    Result findTagDetailById(Long id);
}
