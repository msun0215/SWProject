package com.example.BoardDBRestAPIBySpring.repository;

import com.example.BoardDBRestAPIBySpring.domain.PostFile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, Long> {

	Optional<PostFile> findByName(final String name);

	@Query("SELECT ps from PostFile ps join fetch ps.board b where ps.board.board_no = :board_no")
	List<PostFile> findPostFilesByBoardNo(@Param(value="board_no") final Long board_no);
}
