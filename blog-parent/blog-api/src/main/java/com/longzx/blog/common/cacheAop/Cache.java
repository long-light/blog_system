package com.longzx.blog.common.cacheAop;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    //缓存过期时间
    long expire() default 60 * 1000;
    //缓存存入内存后，需要使用key来获取，这个name就是key
    String name() default "";
}
