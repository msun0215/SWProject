package com.example.BoardDBRestAPIBySpring.domain;

import java.util.Arrays;

public enum RoleName {
    USER("USER"),
    MANAGER("MANAGER"),
    ADMIN("ADMIN"),
    ;

    private final String value;

    RoleName(final String value) {
        this.value = value;
    }

    public static RoleName findBy(final String name) {
        return Arrays.stream(values())
                .filter(roleName -> roleName.value.equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 역할입니다."));
    }

    @Override
    public String toString() {
        return value;
    }
}
