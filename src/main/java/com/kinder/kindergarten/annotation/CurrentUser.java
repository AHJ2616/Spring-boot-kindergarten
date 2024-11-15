package com.kinder.kindergarten.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Documented
public @interface CurrentUser {    
    boolean errorOnInvalidType() default true;
} 
//  nullable = true 만들어 주기
// CustomAuthenticationPrincipalArgumentResolver.java 와 연결됨