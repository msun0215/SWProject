package com.example.BoardDBRestAPIBySpring.domain;

public interface Ownable {

    boolean isOwner(final Member member);
}
