package com.example.BoardDBRestAPIBySpring.request;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PostTitleValidator implements ConstraintValidator<PostTitle, String> {

    private static final String forbiddenWord = "[권한 변경]";

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return !value.startsWith(forbiddenWord);
    }
}
