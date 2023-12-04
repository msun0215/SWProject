package com.example.BoardDBRestAPIBySpring.repository;

import com.example.BoardDBRestAPIBySpring.domain.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {

    Page<Reply> findAllByBoardId(final long boardId, final Pageable pageable);
}
