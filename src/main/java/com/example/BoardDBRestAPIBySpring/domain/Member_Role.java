package com.example.BoardDBRestAPIBySpring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Member_Role {
    @Id
    @Column(updatable = false)  // 수정 불가
    private int member_role_id;
    @Column(updatable = false)  // 수정 불가
    private String role;
}
