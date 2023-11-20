package com.example.BoardDBRestAPIBySpring.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
	@InjectMocks
	private PostService postService;
	@Mock
	private PostRepository postRepository;
	@Mock
	private MemberRepository memberRepository;

	@Test
	void getAllPostsTest() {
		// given
		Member member = Member.builder()
			.member_name("park")
			.build();
		doReturn(member).when(memberRepository).save(any(Member.class));
		memberRepository.save(member);

		List<Board> expected = new ArrayList<>();
		for (long i = 1; i <= 5; i++) {
			expected.add(Board.builder()
				.title("제목입니다." + i)
				.text("내용입니다." + i)
				.count(0)
				.del_flg(0)
				.reg_dtm(LocalDate.now())
				.mod_dtm(LocalDate.now())
				.mem_id("kim")
				.build());
		}
		doReturn(expected).when(postRepository).findAll();

		// when
		List<Board> boards = postService.findAllPosts();

		// then
		assertEquals(5, boards.size());
	}

	@Test
	void getAllPostsByPagingTest() {
		// given
		Member member = Member.builder()
			.member_id("park")
			.build();
		doReturn(member).when(memberRepository).save(any(Member.class));
		memberRepository.save(member);

		List<Board> expected = LongStream.rangeClosed(1, 20)
			.mapToObj(index -> Board.builder()
				.title("제목입니다." + index)
				.text("내용입니다." + index)
				.count(0)
				.del_flg(0)
				.reg_dtm(LocalDate.now())
				.mod_dtm(LocalDate.now())
				.mem_id("kim")
				.build())
			.toList();

		PageRequest pageRequest = PageRequest.of(0, 5);
		doReturn(expected).when(postRepository).findAll(pageRequest).getContent();

		// when
		List<Board> boardsByPaging = postService.findAllPostsByPaging(pageRequest);

		// then
		assertEquals(5, boardsByPaging.size());
	}
}