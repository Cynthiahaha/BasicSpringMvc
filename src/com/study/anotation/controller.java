package com.study.anotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)//当前的注解使用的作用范围
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface controller {
    String value() default "";
}
