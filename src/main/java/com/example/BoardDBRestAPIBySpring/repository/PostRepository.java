package com.example.BoardDBRestAPIBySpring.repository;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Board, Long> {

}
