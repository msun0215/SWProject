package com.example.BoardDBRestAPIBySpring.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.PostFile;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PostFileRepositoryTest {

	@Autowired
	private PostFileRepository postFileRepository;

	@Autowired
	private PostRepository postRepository;

	@BeforeEach
	void init() {
		postFileRepository.deleteAll();
		postRepository.deleteAll();
	}

	@Test
	@DisplayName("save test with datajpatest")
	void saveTest() {
		// given
		Board board = Board.builder()
			.title("제목입니다.")
			.text("내용입니다.")
			.count(0)
			.del_flg(0)
			.reg_dtm(LocalDate.now())
			.mod_dtm(LocalDate.now())
			.mem_id("a")
			.build();
		Board result = postRepository.save(board);

		PostFile postFile = PostFile.builder()
			.name("name")
			.path("path")
			.build();
		postFile.setBoard(board);

		// when
		postFileRepository.save(postFile);

		// then
		assertEquals(1L, board.getBoard_no());
		assertEquals(board, result);
	}
}