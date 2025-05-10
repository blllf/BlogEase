package com.blllf.blogease.validation;

import com.blllf.blogease.anno.State;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StateValidation implements ConstraintValidator<State , String> {
    /**
     *
     * @param s 将来要校验的数据
     * @param constraintValidatorContext
     * @return  如果返回true校验通过
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        //提供校验规则
        if (s == null){
            return false;
        }
        return s.equals("已发布") || s.equals("草稿") || s.equals("发布中") || s.equals("未通过");
    }
}
