<p align=center>
    <img src="https://github.com/long-light/blog_system/blob/master/imags/logoV2.png" alt="LongzxCoding" style="border-radius: 50%">
</p>

<p align=center>
   基于Springboot + Vue 开发的前后端分离博客
</p>


**此项目基于SpringBoot框架开发，实现多用户在线发帖、删帖、评论、回复等功能；搭建后台管理系统，实现对网
站数据的统一管理、用户权限管理**
## 目录结构
前端项目位于blog-app下

后端项目位于blog-parent下，blog-parent/blog-api为后端前台，blog-admin为后端后台管理系统(有待完善)

```
blog-parent
├── blog-admin        --  后台管理
├── blog-api          --  后台前台
    ├── config        --  配置
    ├── comment       --  统一日志、缓存管理
    ├── cotroller     --  控制器
    ├── dao           --  与数据库进行交互的实体类
    ├── handler       --  拦截器模块
    ├── service       --  服务模块
    ├── util          --  工具类模块
    └── vo            --  vo模块
```

## 运行
### 前端工程：需要安装Node.js
```
npm install
npm run build
npm run dev
```
### 后端工程
修改blog-api下**resources/application**中相关配置信息

## 技术介绍

**前端：** vue + vuex + vue-router + axios + element

**后端：** SpringBoot + SpringSecurity + MyBatisPlus + Mysql + Redis + RocketMQ

## 开发环境

|开发工具|说明|
|-|-|
|IDEA|Java开发工具IDE|
|Hbuilder X|Vue开发工具IDE|
|SQLyog|MySQL远程连接工具|
|Another Redis Desktop Manager|Redis远程连接工具|
|X-shell|Linux远程连接工具|
|Xftp|Linux文件上传工具|

## 界面图
### 博客系统主界面
<img src=https://github.com/long-light/blog_system/blob/master/imags/2022131548.png width=90%/>

### 最热标签、最热文章、最新文章
<img src=https://github.com/long-light/blog_system/blob/master/imags/2022141642.png width=90%/>

### 文章归档界面
<img src=https://github.com/long-light/blog_system/blob/master/imags/2022141833.png width=90%/>

### 文章发布界面
<img src=https://github.com/long-light/blog_system/blob/master/imags/2022141943_publish.png width=90%/>

### 文章详情界面
<img src=https://github.com/long-light/blog_system/blob/master/imags/2022142037_ArticleDetial.png width=90%/>
