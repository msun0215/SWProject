package com.example.BoardDBRestAPIBySpring.repository;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// CRUD 함수를 JpaRepository가 가지고 있다.
// @Repository라는 annotation이 없어도 IoC가 된다.
// JpaRepository를 상속했기 때문에
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // findBy는 규칙=>Username문법
    // select * from USER where username=?
    public Member findByMemberID(String memberID);  // JPA Query Method
}
