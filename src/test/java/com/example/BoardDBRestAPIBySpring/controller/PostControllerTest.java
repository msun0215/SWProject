package com.example.BoardDBRestAPIBySpring.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.BoardDBRestAPIBySpring.config.AbstractRestDocsTest;
import com.example.BoardDBRestAPIBySpring.config.jwt.JWTProperties;
import com.example.BoardDBRestAPIBySpring.config.jwt.TokenUtils;
import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.request.PostCreateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PostControllerTest extends AbstractRestDocsTest {
	@Autowired
	private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
	private PostRepository postRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Member member;

    @BeforeEach
    void init() {
        var memberID = "test@test.com";
        var memberPW = "test";

        member = new Member();
        member.setMemberID(memberID);
        member.setMemberPW(bCryptPasswordEncoder.encode(memberPW));
        member.setMemberName("test");
        member.setMemberNickname("testk");
        var role = new Role();
        role.setRoleName("USER");
        roleRepository.save(role);
        member.setRoles(role);

        memberRepository.save(member);
    }

	@Test
	void getAllBoards() throws Exception {
		// given
        var url = "/boards";

		var boards = LongStream.rangeClosed(1, 20)
			.mapToObj(index -> {
                Board board = Board.from("제목입니다." + index, "내용입니다." + index);
                board.setMember(member);
                return board;
            })
			.toList();
		postRepository.saveAll(boards);

		// expected
        mockMvc.perform(get(url)
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("page").description("페이지 인덱스"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("postResponses").description("조회된 게시글"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 게시글 수"),
                                fieldWithPath("pageNumber").description("현재 페이지 번호"),
                                fieldWithPath("pageSize").description("현재 페이지 크기")
                        )
                ));
	}

    @Test
    void findBoard() throws Exception {
        // given
        var url = "/boards/{id}";
        var boardId = 1L;

        var boards = LongStream.rangeClosed(1, 20)
                .mapToObj(index -> {
                    Board board = Board.from("제목입니다." + index, "내용입니다." + index);
                    board.setMember(member);
                    return board;
                })
                .toList();
        postRepository.saveAll(boards);

        // expected
        mockMvc.perform(get(url, boardId))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("id").description("게시글 번호")
                        ),
                        responseFields(
                                fieldWithPath("id").description("번호"),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("createDate").description("생성일"),
                                fieldWithPath("modifyDate").description("수정일"),
                                fieldWithPath("memberName").description("작성자 이름")
                        )
                ));
    }

    @Test
    void createBoard() throws Exception {
        // given
        var url = "/boards";

        var postCreateRequest = PostCreateRequest.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        var jwtToken = TokenUtils.generateJwtToken(member);
        var authorizationHeader = JWTProperties.TOKEN_PREFIX.concat(jwtToken);

        var json = objectMapper.writeValueAsString(postCreateRequest);

        // expected
        mockMvc.perform(post(url)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization").description("JWT Token")
                        ),
                        requestFields(
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용")
                        )
                ));
    }
}