package com.example.BoardDBRestAPIBySpring.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MemberResponseDTO {
    private final List<Member> memberList;
}
