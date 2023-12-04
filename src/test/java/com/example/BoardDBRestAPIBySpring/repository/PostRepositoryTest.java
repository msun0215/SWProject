package com.example.BoardDBRestAPIBySpring.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.BoardDBRestAPIBySpring.config.db.DatabaseClearExtension;
import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
class PostRepositoryTest {

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("게시글 생성 테스트")
	void saveBoardTest() {
		// given
        Member member = new Member();
        member.setMemberID("test@test.com");
        member.setMemberName("test");
        memberRepository.save(member);

        Board board = Board.from("제목입니다.", "내용입니다.");
		board.setMember(member);

		// when
		Board result = postRepository.save(board);

		// then
        assertAll(() -> {
            assertEquals(1L, board.getId());
            assertEquals(member.getMemberName(), board.getMember().getMemberName());
            assertEquals(board, result);
        });
	}
}