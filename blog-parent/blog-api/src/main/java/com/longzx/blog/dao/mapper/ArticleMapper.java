package com.longzx.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longzx.blog.dao.dos.Archives;
import com.longzx.blog.dao.pojo.Article;
import com.longzx.blog.service.AutoTaskService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//我们在application.properties文件中配置了前缀ms_,
//MybatisPlus的BaseMapper通过传入的泛型<Article>会自动将这个Mapper与ms_article关联起来，不区分大小写
//BaseMapper里面封装了很多CRUD语句
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 文章归档的API
     * @return
     */
    List<Archives> listArchives();

    /**
     * 自定义文章查询API
     * @param page
     * @param categoryId
     * @param tagId
     * @param year
     * @param month
     * @return
     */
    IPage<Article> listArticle(
            Page<Article> page,
            Long categoryId,
            Long tagId,
            String year,
            String month);

    void bathUpdateArticleViewCount(List<AutoTaskService.CountQuery> list);

    void bathUpdateArticleCommentCount(List<AutoTaskService.CountQuery> list);

    void bathUpdateArticleScore(List<AutoTaskService.CountQuery> list);
}
