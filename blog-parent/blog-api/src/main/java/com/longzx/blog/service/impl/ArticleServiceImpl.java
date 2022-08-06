package com.longzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.longzx.blog.dao.dos.Archives;
import com.longzx.blog.dao.mapper.ArticleBodyMapper;
import com.longzx.blog.dao.mapper.ArticleMapper;
import com.longzx.blog.dao.mapper.ArticleTagMapper;
import com.longzx.blog.dao.pojo.Article;
import com.longzx.blog.dao.pojo.ArticleBody;
import com.longzx.blog.dao.pojo.ArticleTag;
import com.longzx.blog.dao.pojo.SysUser;
import com.longzx.blog.service.*;
import com.longzx.blog.utils.RedisKeyUtils;
import com.longzx.blog.utils.UserThreadLocal;
import com.longzx.blog.vo.*;
import com.longzx.blog.vo.params.ArticleBodyParam;
import com.longzx.blog.vo.params.ArticleParams;
import org.joda.time.DateTime;
import com.longzx.blog.vo.params.PageParams;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    TagService tagService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    ArticleBodyMapper articleBodyMapper;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ArticleTagMapper articleTagMapper;

    @Autowired
    ThreadService threadService;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    private final String HotScoreKey = RedisKeyUtils.getHotScoreKey();

    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        IPage<Article> articleIPage = articleMapper.listArticle(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());

        List<Article> records = articleIPage.getRecords();
        return Result.success(copyList(records, true, true));
    }

    //    @Override
//    public Result listArticle(PageParams pageParams) {
//        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
//        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
//
//        if (pageParams.getCategoryId()!=null) {
//            //and category_id=#{categoryId}
//            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
//        }
//        List<Long> articleIdList = new ArrayList<>();
//        if(pageParams.getTagId() != null){
//            System.out.println(pageParams.getTagId());
//            //加入标签条件查询
//            //article表中并没有tag字段, 而是通过article_tag这个表将article与tag关联起来，article_id : tag_id -> 1 : n
//            //我们需要利用一个全新的属于文章标签的articleTagLambdaQueryWrapper将这个tagId所对应的articleId都查出来，保存到一个list当中。
//            //然后再根据queryWrapper的in方法选择我们需要的标签即可。
//
//            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
//            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId,pageParams.getTagId());
//            List<ArticleTag> articleTags = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
//            for (ArticleTag articleTag : articleTags) {
//                articleIdList.add(articleTag.getArticleId());
//            }
//            if (articleTags.size() > 0) {
//                // and id in(1,2,3)
//                queryWrapper.in(Article::getId,articleIdList);
//            }
//        }
//
//        queryWrapper.orderByDesc(Article::getWeight);
//        queryWrapper.orderByDesc(Article::getCreateDate);
//        Page<Article> pages = articleMapper.selectPage(page, queryWrapper);
//        List<Article> records = pages.getRecords();
//        List<ArticleVo> articleVoList = copyList(records, true, true);
//        return Result.success(articleVoList);
//    }

    /**
     * 根据view_counts进行降序排列来获取前articleLimits个最热文章的id和title
     * @param articleLimits
     * @return
     */
    @Override
    public Result hotArticles(int articleLimits) {
        /**
         * 计算帖子的分数：log(评论数*10 + 浏览数*2) + 发布时间 - 祖祥纪元
         * Article拥有一个weight属性，表示该文章的得分
         * 首先按照得分排序，得分相同时按照浏览量返回，返回前6篇文章
         */
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        String hotScoreKey = RedisKeyUtils.getHotScoreKey();
        Set<Object> objects = redisTemplate.opsForZSet().reverseRange(hotScoreKey, 0, articleLimits);
        if (objects != null && !objects.isEmpty()){
            queryWrapper.in(Article::getId, objects);
            queryWrapper.select(Article::getId, Article::getTitle);
        } else {
            queryWrapper.orderByDesc(Article::getViewCounts);
            queryWrapper.select(Article::getId, Article::getTitle);
            queryWrapper.last("limit " + articleLimits);
        }
        List<Article> articleList = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articleList, false, false));
    }

    /**
     * 根据create_date进行降序排列来获取前articleLimits个最热文章的id和title
     * @param articleLimits
     * @return
     */
    @Override
    public Result newArticles(int articleLimits) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit "+articleLimits);
        List<Article> articleList = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articleList, false, false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archives = articleMapper.listArchives();
        return Result.success(archives);
    }

    @Override
    public Result findArticleById(long id) {
        /**
         * 1.根据文章信息查询id
         * 2.查看文章之后，需要将文章阅读数+1
         * 3.更新文章阅读数时需要对ms_article进行写表，此时会对表加写锁，阻塞读的操作，造成性能瓶颈(当前session可以查询、更新；其他session阻塞读、写)
         * 4.此外，更新会造成此次接口请求处理的耗时，并且如果更新出问题了之后，不能正常返回文章内容
         * 5.解决方法是使用线程池技术，把更新操作用线程池中的线程执行，这样才不会影响主线程的执行
         */

        Article article = articleMapper.selectById(id);
        ArticleVo articleVo = copy(article, true, true, true, true);
        //更新阅读数使用线程池的方式
        //threadService.updateArticleViewCount(articleMapper, article);

        //使用redis固化的方式
        String redisKey ="VIEW_COUNT" + id;
        String hotScoreKey = RedisKeyUtils.getHotScoreKey();
        //如果Redis中不存在则进行设置
        redisTemplate.opsForValue().setIfAbsent(redisKey, article.getViewCounts(), 5, TimeUnit.MINUTES);
        redisTemplate.opsForZSet().addIfAbsent(hotScoreKey, id, article.getWeight());
        //设置redisKey这个对应的值自增1
        redisTemplate.opsForValue().increment(redisKey,1);
        redisTemplate.opsForZSet().incrementScore(hotScoreKey, id, 1);

        return Result.success(articleVo);
    }

    /**
     * 发布文章
     * @param articleParam 文章的相关信息
     * @return 返回值使用Result封装文章在数据库中自动给出的id，前端会根据这个id跳转到该文章的详情页面
     */
    @Override
    public Result publish(ArticleParams articleParam) {
        /**
         * 1.获取当前用户信息，从UserThreadLocal中获取->调用这个方法的请求必须要被请求拦截器拦截
         * 2.构建ms_article实体类对象，设置属性值，将对象insert入ms_article
         * 3.获取新建文章的article_id
         * 4.构建ms_article_body实体类对象，设置属性值，将对象insert入ms_article_body
         * 5.ms_article_tag
         */

        SysUser sysUser = UserThreadLocal.get();

        Article article;
        boolean isEdit = false;
        //articleParam不为空表示是修改文章
        if (articleParam.getId() != null){
            article = new Article();
            article.setId(articleParam.getId());
            article.setTitle(articleParam.getTitle());
            article.setSummary(articleParam.getSummary());
            article.setCategoryId(articleParam.getCategory().getId());
            articleMapper.updateById(article);
            isEdit = true;
        }else{
            article = new Article();
            article.setAuthorId(sysUser.getId());
            article.setWeight(Article.Article_Common);
            article.setViewCounts(0);
            article.setTitle(articleParam.getTitle());
            article.setSummary(articleParam.getSummary());
            article.setCommentCounts(0);
            article.setCreateDate(System.currentTimeMillis());
            article.setCategoryId(articleParam.getCategory().getId());
            /**
             * 插入之后, article的id属性会被赋值
             * 官网解释："insart后主键会自动'set到实体的ID字段。所以你只需要getid()就可以获得数据库中的id值
             * 利用主键自增，mybatis-plus的insert操作后id值会回到参数对象中
             */
            this.articleMapper.insert(article);
        }

        //增加ms_article_tag表
        List<TagVo> tags = articleParam.getTags();
        if (tags != null){
            for (TagVo tag : tags) {
                Long articleId = article.getId();
                if (isEdit){
                    //先删除
                    LambdaQueryWrapper<ArticleTag> queryWrapper = Wrappers.lambdaQuery();
                    queryWrapper.eq(ArticleTag::getArticleId,articleId);
                    articleTagMapper.delete(queryWrapper);
                }
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(tag.getId());
                articleTag.setArticleId(articleId);
                articleTagMapper.insert(articleTag);
            }
        }

        //增加ms_article_body表
        if (isEdit){
            ArticleBody articleBody = new ArticleBody();
            articleBody.setArticleId(article.getId());
            articleBody.setContent(articleParam.getBody().getContent());
            articleBody.setContentHtml(articleParam.getBody().getContentHtml());
            LambdaUpdateWrapper<ArticleBody> updateWrapper = Wrappers.lambdaUpdate();
            updateWrapper.eq(ArticleBody::getArticleId,article.getId());
            articleBodyMapper.update(articleBody, updateWrapper);
        }else {
            ArticleBody articleBody = new ArticleBody();
            articleBody.setArticleId(article.getId());
            articleBody.setContent(articleParam.getBody().getContent());
            articleBody.setContentHtml(articleParam.getBody().getContentHtml());
            articleBodyMapper.insert(articleBody);

            article.setBodyId(articleBody.getId());
            articleMapper.updateById(article);
        }

        //返回文章id值给前端
        ArticleVo articleVo = new ArticleVo();
        articleVo.setId(article.getId());

        return Result.success(articleVo);
    }

    @Override
    public Result searchArticle(String search) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId,Article::getTitle);
        queryWrapper.like(Article::getTitle,search);
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);

        return Result.success(copyList(articles,false,false));
    }

    public ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory){
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        //从redis中取出相关数据
        Integer comment = (Integer) redisTemplate.opsForValue().get("COMMENT_COUNT" + article.getId());
        if (comment != null) articleVo.setCommentCounts(comment);
        Integer view = (Integer) redisTemplate.opsForValue().get("VIEW_COUNT" + article.getId());
        if (view != null) articleVo.setViewCounts(view);

        if(isTag){
            Long articleId = article.getId();
            articleVo.setTags(tagService.findArticleTagById(articleId));
        }
        if(isAuthor){
            Long authorId = article.getAuthorId();
            SysUser user = sysUserService.findUserById(authorId);
            UserVo userVo = new UserVo();
            userVo.setAvatar(user.getAvatar());
            userVo.setId(user.getId());
            userVo.setNickname(user.getNickname());
            articleVo.setAuthor(userVo);
        }
        if(isBody){
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if(isCategory){
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));
        }
        return articleVo;
    }

    private ArticleBodyVo findArticleBodyById(Long bodyId) {
        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            ArticleVo articleVo = copy(article, isTag, isAuthor, false, false);
            articleVoList.add(articleVo);
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            ArticleVo articleVo = copy(article, isTag, isAuthor, isBody, isCategory);
            articleVoList.add(articleVo);
        }
        return articleVoList;
    }
}
