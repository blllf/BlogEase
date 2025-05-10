package com.blllf.blogease.anno;


import com.blllf.blogease.validation.StateValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)  //元注解 表明该注解是作用到哪个位置上
@Retention(RetentionPolicy.RUNTIME)     //表示该注解会在哪个阶段保留
@Constraint(validatedBy = {StateValidation.class})     //谁给这个注解提供校验规则
public @interface State {
    String message() default "state参数的值只能是已发布或者草稿";
    //指定分组
    Class<?>[] groups() default {};
    //负载，获取State 附加信息
    Class<? extends Payload>[] payload() default {};

}
