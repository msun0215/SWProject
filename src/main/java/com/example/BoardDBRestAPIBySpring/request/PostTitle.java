package com.example.BoardDBRestAPIBySpring.request;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PostTitleValidator.class)
@Documented
public @interface PostTitle {

    String message() default "Invalid Post Title";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};
}
