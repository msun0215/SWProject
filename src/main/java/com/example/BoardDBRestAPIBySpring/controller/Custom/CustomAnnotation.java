package com.example.BoardDBRestAPIBySpring.controller.Custom;



import jakarta.validation.Payload;
import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

public class CustomAnnotation {

    @Documented
    @Constraint(validatedBy = EmailValidator.class)
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    public @interface CustomEmail {
        String message() default "로컬 최대 64자, 로컬에서 밑줄(_) 하이픈(-) 점(.) 허용, " +
                "로컬 시작과 끝에 점(.) 사용 불가능, 로컬 점(.) 연속 두 개 사용 불가능";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }

    @Documented
    //@Constraint(validatedBy = PasswordValidator.class)
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    public @interface Password {
        String message() default "최소 8자 및 최대 20자, 대문자 하나 이상, " +
                "소문자 하나 이상, 숫자 하나 및 특수 문자 하나 이상";
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
}
