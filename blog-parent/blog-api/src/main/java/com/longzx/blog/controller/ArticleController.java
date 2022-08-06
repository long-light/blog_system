package com.longzx.blog.controller;

import com.longzx.blog.common.cacheAop.Cache;
import com.longzx.blog.common.logAop.LogAnnotation;
import com.longzx.blog.service.ArticleService;
import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.params.ArticleParams;
import com.longzx.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    ArticleService articleService;

    /**
     * 首页展示文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    @LogAnnotation(module = "文章", operation = "获取文章列表") //aop增强切点注解，这里对方法进行日志增强
    public Result listArticle(@RequestBody PageParams pageParams){
        return articleService.listArticle(pageParams);
    }

    /**
     * 展示最热文章
     * @return
     */
    @PostMapping("/hot")
    @Cache(expire = 5 * 60 * 1000, name = "HotArticle")
    public Result hotArticles(){
        int articleLimits = 6;
        return articleService.hotArticles(articleLimits);
    }

    /**
     * 展示最新文章
     * @return
     */
    @PostMapping("/new")
    //@Cache(expire = 5 * 60 * 1000,name = "new_article")
    public Result newArticles(){
        int articleLimits = 5;
        return articleService.newArticles(articleLimits);
    }

    /**
     * 文章归档
     * @return
     */
    @PostMapping("/listArchives")
    public Result listArchives(){
        return articleService.listArchives();
    }

    /**
     * 查看文章内容
     * @param id
     * @return
     */
    @PostMapping("/view/{id}")
    public Result viewArticle(@PathVariable("id") long id){
        return articleService.findArticleById(id);
    }

    /**
     * 发布文章
     * @param articleParams
     * @return
     */
    @PostMapping("/publish")
    public Result publish(@RequestBody ArticleParams articleParams){
        return articleService.publish(articleParams);
    }

    /**
     * 修改文章
     * 1.根据文章id将待修改的文章信息返回给前端，在写文章界面进行展示
     * 2.最后还是访问/publish这个接口，因此需要修改一下publish这个控制器方法
     * @param id
     * @return
     */
    @PostMapping("/{id}")
    public Result editArticle(@PathVariable("id") Long id){
        return articleService.findArticleById(id);
    }

    /**
     * 搜索文章
     * @param articleParam
     * @return
     */
    @PostMapping("/search")
    public Result search(@RequestBody ArticleParams articleParam){
        //写一个搜索接口
        String search = articleParam.getSearch();
        return articleService.searchArticle(search);
    }
}
