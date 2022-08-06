package com.longzx.blog.service.impl;

import com.longzx.blog.dao.mapper.TagMapper;
import com.longzx.blog.dao.pojo.Tag;
import com.longzx.blog.service.TagService;
import com.longzx.blog.vo.Result;
import com.longzx.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    TagMapper tagMapper;

    public TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
    public List<TagVo> copyList(List<Tag> tagList){
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }

    @Override
    public List<TagVo> findArticleTagById(Long articleId) {
        //mybatis-plus无法进行多表查询，数据库中通过article_tag将article和tag两张表关联起来
        List<Tag> tags = tagMapper.findArticleTagById(articleId);

        return copyList(tags);
    }

    /**
     * 根据tag_id进行分组查询，计数，然后按照从小到大排序，返回前hotTagsLimits个
     * 再根据tag_id查询tag_name
     * @param hotTagsLimits
     * @return
     */
    @Override
    public Result hotTags(int hotTagsLimits) {
        //根据tag_id进行分组查询，计数，然后按照从小到大排序，返回前hotTagsLimits个
        List<Long> tagIds = tagMapper.findHotsTagIds(hotTagsLimits);
        //再根据tag_id查询tag_name
        List<Tag> tagNames = tagMapper.findTagNameByIds(tagIds);
        return Result.success(copyList(tagNames));
    }

    @Override
    public Result findAll() {
        List<Tag> tags = tagMapper.selectList(null);
        return Result.success(copyList(tags));
    }

    @Override
    public Result findTagsDetail() {
        List<Tag> tags = tagMapper.selectList(null);
        return Result.success(copyList(tags));
    }

    @Override
    public Result findTagDetailById(Long id) {
        Tag tag = tagMapper.selectById(id);
        return Result.success(copy(tag));
    }
}
