package com.example.BoardDBRestAPIBySpring.repository;

import com.example.BoardDBRestAPIBySpring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

}
