package com.longzx.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.longzx.blog.dao.pojo.Tag;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TagMapper extends BaseMapper<Tag> {
    /**
     * 根据文章ID查询文章的tag标签
     * @param articleId
     * @return
     */
    List<Tag> findArticleTagById(Long articleId);

    /**
     * 查询最热标签
     * @param hotTagsLimits
     * @return
     */
    List<Long> findHotsTagIds(int hotTagsLimits);

    /**
     * 根据tagIds来查询tag_names
     * @param tagIds
     * @return
     */
    List<Tag> findTagNameByIds(List<Long> tagIds);
}
