package com.longzx.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.longzx.blog.dao.mapper.ArticleMapper;
import com.longzx.blog.utils.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class AutoTaskService {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ArticleMapper articleMapper;

    @Scheduled(cron = "30 * * * * ?") //每天30秒执行一次固化任务
    public void updateViewCount(){
        log.info("==============更新文章阅读数==============");
        Set<String> keys = redisTemplate.keys("VIEW_COUNT"+"*");
        List<CountQuery> list = new ArrayList<>();
        if(keys != null && !keys.isEmpty()){
            for (String key : keys) {
                CountQuery query = new CountQuery();
                String ArticleIdStr = key.substring("VIEW_COUNT".length());
                Integer viewCount = (Integer) redisTemplate.opsForValue().get(key);
                log.info("更新文章" + ArticleIdStr + "的阅读数为：" +viewCount);
                long articleId = Long.parseLong(ArticleIdStr);
                query.setArticleId(articleId);
                query.setCount(viewCount);
                list.add(query);
            }
        }
        if (list.size()>0) {
            //为确保缓存一致性，更新数据库之后需要删除缓存数据
            articleMapper.bathUpdateArticleViewCount(list);
            redisTemplate.delete("VIEW_COUNT"+"*");
        }
    }

    @Scheduled(cron = "30 * * * * ?")
    public void updateCommentCounts(){
        log.info("==============更新文章评论数==============");
        Set<String> keys = redisTemplate.keys("COMMENT_COUNT"+"*");
        List<CountQuery> list = new ArrayList<>();
        if(keys != null && !keys.isEmpty()){
            for (String key : keys) {
                CountQuery query = new CountQuery();
                String ArticleIdStr = key.substring("COMMENT_COUNT".length());
                Integer commentCount = (Integer) redisTemplate.opsForValue().get(key);
                log.info("更新文章" + ArticleIdStr + "的评论数为：" +commentCount);
                long articleId = Long.parseLong(ArticleIdStr);
                query.setArticleId(articleId);
                query.setCount(commentCount);
                list.add(query);
            }
        }
        if (list.size()>0) {
            articleMapper.bathUpdateArticleCommentCount(list);
            redisTemplate.delete("COMMENT_COUNT"+"*");
        }
    }

    @Scheduled(cron = "30 * * * * ?")
    public void updateWeight(){
        log.info("==============更新文章分数==============");
        String hotScoreKey = RedisKeyUtils.getHotScoreKey();
        Set<Object> set = redisTemplate.opsForZSet().reverseRange(hotScoreKey, 0, -1);
        List<CountQuery> list = new ArrayList<>();

        if(set != null && !set.isEmpty()){
            for (Object key : set) {
                CountQuery query = new CountQuery();
                Double score = redisTemplate.opsForZSet().score(hotScoreKey, key);
                log.info("更新文章" + key + "的分数为：" + score);
                //key: Object - > Long
                long articleId = Long.parseLong(String.valueOf(key));
                query.setArticleId(articleId);
                query.setCount(score.intValue());
                list.add(query);
            }
        }
        if (list.size()>0) {
            articleMapper.bathUpdateArticleScore(list);
            //redisTemplate.delete("COMMENT_COUNT"+"*");
        }
    }



    public static class CountQuery{

        private Long articleId;

        private Integer Count;

        public CountQuery() {
        }

        public Long getArticleId() {
            return articleId;
        }

        public void setArticleId(Long articleId) {
            this.articleId = articleId;
        }

        public Integer getCount() {
            return Count;
        }

        public void setCount(Integer Count) {
            this.Count = Count;
        }
    }

}
