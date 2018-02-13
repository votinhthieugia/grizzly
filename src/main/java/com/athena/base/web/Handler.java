package com.athena.base.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handler {
    boolean async() default false;
    String method();
    String route();
    Class<?> requestModel();
    Class<?> responseModel();
}
