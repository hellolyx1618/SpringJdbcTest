package com.lyx.base.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用该注解标注主键列，并指明映射关系。
 * 必须标注到一个相应的field上。
 * 必须标注到表的主键上
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface PrimaryKey {
    String name() default "";
}
