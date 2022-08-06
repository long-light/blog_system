# blog_system
**此项目基于SpringBoot框架开发，实现多用户在线发帖、删帖、评论、回复等功能；搭建后台管理系统，实现对网
站数据的统一管理、用户权限管理。**
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

![界面](images/2022131548.png)
