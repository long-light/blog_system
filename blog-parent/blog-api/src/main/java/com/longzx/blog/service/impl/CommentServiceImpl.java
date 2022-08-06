package com.longzx.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longzx.blog.dao.mapper.ArticleMapper;
import com.longzx.blog.dao.mapper.CommentMapper;
import com.longzx.blog.dao.pojo.Article;
import com.longzx.blog.dao.pojo.Comment;
import com.longzx.blog.dao.pojo.SysUser;
import com.longzx.blog.service.*;
import com.longzx.blog.utils.RedisKeyUtils;
import com.longzx.blog.utils.UserThreadLocal;
import com.longzx.blog.vo.CommentVo;
import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.UserVo;
import com.longzx.blog.vo.params.CommentParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    ThreadService threadService;
    @Autowired
    SensitiveFilter sensitiveFilter;
    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result commentsArticleById(Long id) {
        /**
         * 1.根据文章id查询文章的评论
         * 2.根据评论作者id来查找作者信息
         * 3.如果该评论的level是1，则查找其子评论，通过parent_id = id(评论id)
         */

        //查询文章对应的评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, id);
        queryWrapper.eq(Comment::getLevel, 1);

        List<Comment> comments = commentMapper.selectList(queryWrapper);
        //将comment转换成commentVo
        List<CommentVo> commentVoList = copyList(comments);

        return Result.success(commentVoList);
    }

    @Override
    public Result commentToArticle(CommentParams commentParams) {
        /**
         * 1.根据文章id,在ms_article表中，将comment_counts+1;
         * 2.从ThreadLocal中取出用户信息
         * 3.封装在comment对象中，插入数据库
         */

        SysUser sysUser = UserThreadLocal.get();
        Comment comment = new Comment();
        //注入属性
        comment.setAuthorId(sysUser.getId());
        Long articleId = commentParams.getArticleId();
        comment.setArticleId(articleId);
        String filterText = sensitiveFilter.filter(commentParams.getContent());
        comment.setContent(filterText);;
        comment.setCreateDate(System.currentTimeMillis());
        Long parent = commentParams.getParent();
        if(parent == null || parent == 0) comment.setLevel(1);
        else comment.setLevel(2);
        comment.setParentId(parent == null ? 0 : parent);
        comment.setToUid(commentParams.getToUserId() == null ? 0 : commentParams.getToUserId());
        commentMapper.insert(comment);

        //更新文章评论数，基于线程池
        //threadService.updateArticleCommentCounts(articleMapper, commentParams.getArticleId());

        //更新文章评论数，基于Redis+定时任务
        String redisKey = "COMMENT_COUNT" + articleId;
        String hotScoreKey = RedisKeyUtils.getHotScoreKey();
        if (Boolean.FALSE.equals(redisTemplate.hasKey(redisKey))){
            Article article = articleMapper.selectById(articleId);
            redisTemplate.opsForValue().set(redisKey, article.getCommentCounts(), 5, TimeUnit.MINUTES);
            redisTemplate.opsForZSet().addIfAbsent(hotScoreKey, articleId, article.getWeight());

        }
        redisTemplate.opsForValue().increment(redisKey, 1);
        redisTemplate.opsForZSet().incrementScore(hotScoreKey, articleId, 1);

        CommentVo commentVo = copy(comment);
        return Result.success(commentVo);
    }

    private List<CommentVo> copyList(List<Comment> comments) {
        ArrayList<CommentVo> commentVos = new ArrayList<>();
        for (Comment comment : comments) {
            commentVos.add(copy(comment));
        }
        return commentVos;
    }

    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment, commentVo);
        //createDate: Long -> String
        commentVo.setCreateDate(new DateTime(comment.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        //作者信息: UserVo author;
        UserVo userVo = sysUserService.findUserVoById(comment.getAuthorId());
        commentVo.setAuthor(userVo);

        //查找子评论
        Integer level = comment.getLevel();
        if (1 == level){ //只有level为1的评论才有子评论
            Long id = comment.getId();
            List<CommentVo> commentVoList = findCommentsByParentId(id);
            commentVo.setChildrens(commentVoList);
        }
        //to User 给谁评论
        if (level > 1){
            Long toUid = comment.getToUid();
            UserVo toUserVo = this.sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);
        }
        return commentVo;
    }

    private List<CommentVo> findCommentsByParentId(Long id) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId,id);
        queryWrapper.eq(Comment::getLevel,2);
        List<Comment> comments = commentMapper.selectList(queryWrapper);
        return copyList(comments);
    }

}
