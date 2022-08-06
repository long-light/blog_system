package com.longzx.blog.common.logAop;

import java.lang.annotation.*;

//type:可以加载类上， method可以加载方法上
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    String module() default "";

    String operation() default "";
}
