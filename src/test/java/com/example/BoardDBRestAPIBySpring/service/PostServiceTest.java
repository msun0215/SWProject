package com.example.BoardDBRestAPIBySpring.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.BoardDBRestAPIBySpring.config.db.DatabaseClearExtension;
import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
class PostServiceTest {
	@Autowired
	private PostService postService;
    @Autowired
    private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;

	@Test
    @DisplayName("페이징으로 게시글들 조회 테스트")
	void findAllPostsByTest() {
		// given
        var member = new Member();
        member.setMemberID("test@test.com");
        member.setMemberName("test");
        memberRepository.save(member);

		var boards = LongStream.rangeClosed(1, 20)
			.mapToObj(index -> {
                Board board = Board.from("제목입니다." + index, "내용입니다." + index);
                board.setMember(member);
                return board;
            })
			.toList();
		postRepository.saveAll(boards);

		var pageRequest = PageRequest.of(0, 5);

		// when
        var actual = postService.findAllPostsBy(pageRequest);

        // then
        assertAll(() -> {
            assertEquals(boards.size() / pageRequest.getPageSize(), actual.getTotalPages());
            assertEquals(boards.size(), actual.getTotalElements());
            assertEquals(pageRequest.getPageNumber(), actual.getPageNumber());
            assertEquals(pageRequest.getPageSize(), actual.getPageSize());
            assertEquals(pageRequest.getPageSize(), actual.getPostResponses().size());
        });
	}

    @Test
    @DisplayName("게시글 단건 조회 테스트")
    void findPostTest() {
        // given
        var member = new Member();
        member.setMemberID("test@test.com");
        member.setMemberName("test");
        memberRepository.save(member);

        var boards = LongStream.rangeClosed(1, 20)
                .mapToObj(index -> {
                    Board board = Board.from("제목입니다." + index, "내용입니다." + index);
                    board.setMember(member);
                    return board;
                })
                .toList();
        postRepository.saveAll(boards);

        var boardId = 1L;

        // when
        var actual = postService.findById(boardId);

        // then
        assertAll(() -> {
            assertEquals(boardId, actual.getId());
            assertEquals("제목입니다.1", actual.getTitle());
            assertEquals(member.getMemberName(), actual.getMemberName());
        });
    }
}