package com.nihalsoft.java.jbean.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.nihalsoft.java.jbean.BeanScope;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Bean {

    String name() default "";

    BeanScope scope() default BeanScope.SINGLETON;

}
