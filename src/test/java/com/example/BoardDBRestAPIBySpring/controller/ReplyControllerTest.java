package com.example.BoardDBRestAPIBySpring.controller;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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
import com.example.BoardDBRestAPIBySpring.domain.Reply;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.repository.ReplyRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.request.ReplyCreateRequest;
import com.example.BoardDBRestAPIBySpring.request.ReplyEditRequest;
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
class ReplyControllerTest extends AbstractRestDocsTest {
    private final String baseURL = "/boards/{boardId}/replies";
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
    private ReplyRepository replyRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private Member member;
    private Board board;

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

        board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);
    }

    @Test
    void getAllReplies() throws Exception {
        // given
        var boardId = board.getId();

        var replies = LongStream.rangeClosed(1, 20)
                .mapToObj(index -> {
                    var reply = Reply.of("내용입니다." + index);
                    reply.setMember(member);
                    reply.setBoard(board);
                    return reply;
                })
                .toList();
        replyRepository.saveAll(replies);

        // expected
        mockMvc.perform(get(baseURL, boardId)
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("boardId").description("게시글 번호")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 인덱스"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("repliesResponse").description("조회된 댓글"),
                                fieldWithPath("totalPages").description("전체 페이지 수"),
                                fieldWithPath("totalElements").description("전체 게시글 수"),
                                fieldWithPath("pageNumber").description("현재 페이지 번호"),
                                fieldWithPath("pageSize").description("현재 페이지 크기")
                        )
                ));
    }

    @Test
    void createReply() throws Exception {
        // given
        var boardId = board.getId();

        var replyCreateRequest = ReplyCreateRequest.builder()
                .content("내용입니다.")
                .build();
        var json = objectMapper.writeValueAsString(replyCreateRequest);

        var jwtToken = TokenUtils.generateJwtToken(member);
        var authorizationHeader = JWTProperties.TOKEN_PREFIX.concat(jwtToken);

        // expected
        mockMvc.perform(post(baseURL, boardId)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("boardId").description("게시글 번호")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("JWT Token")
                        ),
                        requestFields(
                                fieldWithPath("content").description("내용")
                        )
                ));
    }

    @Test
    void editReply() throws Exception {
        // given
        var url = baseURL.concat("/{id}");
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);
        var replyId = reply.getId();

        var boardId = board.getId();

        var request = ReplyEditRequest.builder()
                .content("수정된 내용입니다.")
                .build();
        var json = objectMapper.writeValueAsString(request);

        var jwtToken = TokenUtils.generateJwtToken(member);
        var authorizationHeader = JWTProperties.TOKEN_PREFIX.concat(jwtToken);

        // expected
        mockMvc.perform(put(url, boardId, replyId)
                        .header("Authorization", authorizationHeader)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization").description("JWT Token")
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시글 번호"),
                                parameterWithName("id").description("수정할 댓글 번호")
                        ),
                        requestFields(
                                fieldWithPath("content").description("내용")
                        )
                ));
    }

    @Test
    void deleteReply() throws Exception {
        // given
        var url = baseURL.concat("/{id}");
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var replyId = reply.getId();
        var boardId = board.getId();

        var jwtToken = TokenUtils.generateJwtToken(member);
        var authorizationHeader = JWTProperties.TOKEN_PREFIX.concat(jwtToken);

        // expected
        mockMvc.perform(delete(url, boardId, replyId)
                        .header("Authorization", authorizationHeader))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                        requestHeaders(
                                headerWithName("Authorization").description("JWT Token")
                        ),
                        pathParameters(
                                parameterWithName("boardId").description("게시글 번호"),
                                parameterWithName("id").description("삭제할 댓글 번호")
                        )
                ));
    }
}