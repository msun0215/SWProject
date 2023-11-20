package com.example.BoardDBRestAPIBySpring.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
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
class PostRepositoryTest {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private MemberRepository memberRepository;

	@BeforeEach
	void init() {
		postRepository.deleteAll();
		memberRepository.deleteAll();
	}

	@Test
	@DisplayName("save test with datajpatest")
	void saveTest() {
		// given
		Member member = Member.builder()
			.name("name")
			.build();
		memberRepository.save(member);

		Board board = Board.builder()
			.title("제목입니다.")
			.text("내용입니다.")
			.count(0)
			.del_flg(0)
			.reg_dtm(LocalDate.now())
			.mod_dtm(LocalDate.now())
			.mem_id("a")
			.build();
		board.setMember(member);

		// when
		Board result = postRepository.save(board);

		// then
		assertEquals(1L, board.getBoard_no());
		assertEquals(board, result);
	}
}