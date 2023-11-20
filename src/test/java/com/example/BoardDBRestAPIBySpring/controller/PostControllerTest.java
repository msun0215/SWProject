package com.example.BoardDBRestAPIBySpring.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void init() {

	}

	@Test
	void getAllBoardsTest() throws Exception {
		// given
		Member member = Member.builder()
			.member_name("park")
			.build();
		memberRepository.save(member);

		List<Board> expected = LongStream.rangeClosed(1, 5)
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
		postRepository.saveAll(expected);

		// expected
		mockMvc.perform(get("/boards"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.*", hasSize(5)))
			.andDo(print());
	}
}