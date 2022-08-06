package com.longzx.blog.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class CommentVo  {

    //当前评论的id
    //分布式id比较长(19位)，传到前端会有精度损失(js只支持16位)，必须转为string类型进行传输，就不会有问题了
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    //评论者的用户信息
    private UserVo author;

    //评论的内容
    private String content;

    //当前评论下的回复评论
    private List<CommentVo> childrens;

    //创建时间
    private String createDate;

    //评论的层级
    private Integer level;

    //这是给哪位用户的评论
    private UserVo toUser;
}


