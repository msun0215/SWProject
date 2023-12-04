//package com.example.BoardDBRestAPIBySpring.service;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//import com.example.BoardDBRestAPIBySpring.domain.Board;
//import com.example.BoardDBRestAPIBySpring.domain.Member;
//import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
//import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.stream.LongStream;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.context.ActiveProfiles;
//
//@ActiveProfiles("test")
//@SpringBootTest
//class PostServiceTest {
//	@Autowired
//	private PostService postService;
//	@Autowired
//	private MemberRepository memberRepository;
//
//	@BeforeEach
//	void init() {
//		memberRepository.deleteAll();
//	}
//
//	@Test
//	void getAllPostsTest() {
//		// given
//		Member member = Member.builder()
//			.member_name("park")
//			.build();
//		memberRepository.save(member);
//
//		List<Board> expected = LongStream.rangeClosed(1, 5)
//			.mapToObj(index -> Board.builder()
//				.title("제목입니다." + index)
//				.text("내용입니다." + index)
//				.count(0)
//				.del_flg(0)
//				.reg_dtm(LocalDate.now())
//				.mod_dtm(LocalDate.now())
//				.mem_id("kim")
//				.build())
//			.toList();
//		postRepository.saveAll(expected);
//
//		// when
//		List<Board> boards = postService.findAllPosts();
//
//		// then
//		assertEquals(expected.size(), boards.size());
//	}
//
//	@Test
//	void getAllPostsByPagingTest() {
//		// given
//		Member member = Member.builder()
//			.member_name("park")
//			.build();
//		memberRepository.save(member);
//
//		List<Board> expected = LongStream.rangeClosed(1, 20)
//			.mapToObj(index -> Board.builder()
//				.title("제목입니다." + index)
//				.text("내용입니다." + index)
//				.count(0)
//				.del_flg(0)
//				.reg_dtm(LocalDate.now())
//				.mod_dtm(LocalDate.now())
//				.mem_id("kim")
//				.build())
//			.toList();
//		postRepository.saveAll(expected);
//
//		PageRequest pageRequest = PageRequest.of(0, 5);
//
//		// when
//		List<Board> boardsByPaging = postService.findAllPostsByPaging(pageRequest);
//
//		// then
//		assertEquals(5, boardsByPaging.size());
//	}
//}