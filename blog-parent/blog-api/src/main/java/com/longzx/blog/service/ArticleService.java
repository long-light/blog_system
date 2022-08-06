package com.longzx.blog.service;

import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.params.ArticleParams;
import com.longzx.blog.vo.params.PageParams;

public interface ArticleService {

    /**
     * 分页查询文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 查询最热文章，最热文章是根据浏览次数来判定的
     * @param articleLimits
     * @return
     */
    Result hotArticles(int articleLimits);

    /**
     * 查询最新文章，最新文章根据创建时间来判定
     * @param articleLimits
     * @return
     */
    Result newArticles(int articleLimits);

    /**
     * 文章按照月份进行归档处理
     * @return
     */
    Result listArchives();

    /**
     * 根据文章id查看文章内容
     * @param id
     * @return
     */
    Result findArticleById(long id);

    /**
     * 发布文章
     * @param articleParams
     * @return
     */
    Result publish(ArticleParams articleParams);

    /**
     * 搜索文章
     * @param search
     * @return
     */
    Result searchArticle(String search);
}
