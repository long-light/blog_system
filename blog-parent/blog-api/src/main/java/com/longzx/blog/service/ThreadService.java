package com.longzx.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longzx.blog.dao.mapper.ArticleMapper;
import com.longzx.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {

    /**
     * 更新指定文章的阅读数，这个线程在线程池中执行
     * @param articleMapper
     * @param article
     */
    @Async("taskExecutor") //使用ThreadPoolConfig中定义的线程池来执行这个函数
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {

        /**
         * //不使用线程池技术，直接使用主线程进行更新viewCount
         * try {
         *             Thread.sleep(5000);
         *         } catch (InterruptedException e) {
         *             e.printStackTrace();
         *         }
         */

        int viewCounts = article.getViewCounts();
        Article newArticle = new Article();
        //在mybatis-plus中，使用实体类对象作为传入数据执行insert/update语句时，对象中为null的值不会被修改
        //但是，切记需要将与表对应的实体类中的基本数据类型值换成包装类，如果使用基本数据类型，会有默认值0
        newArticle.setViewCounts(viewCounts+1);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //没有这条语句的话，就会更新ms_article中的全部记录，而我们只需要更新指定id的文章的浏览次数
        queryWrapper.eq(Article::getId, article.getId());
        //多线程环境下，保证线程安全
        queryWrapper.eq(Article::getViewCounts, article.getViewCounts());
        //update ms_article set view_counts = ? where view_counts = ? and id = ?
        articleMapper.update(newArticle, queryWrapper);
    }

    /**
     *  @param articleMapper:ms_article的服务层
     * @param articleMapper
     * @param articleId : 需要增加评论数的文章id
     */
    @Async("taskExecutor")
    public void updateArticleCommentCounts(ArticleMapper articleMapper, Long articleId) {
        //根据articleId获取现有评论数
        Article article = articleMapper.selectById(articleId);
        Integer commentCounts = article.getCommentCounts();
        Article newArticle = new Article();
        newArticle.setCommentCounts(commentCounts+1);

        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getId, articleId);
        queryWrapper.eq(Article::getCommentCounts, commentCounts);

        articleMapper.update(newArticle, queryWrapper);
    }

}
