package com.example.BoardDBRestAPIBySpring.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.BoardDBRestAPIBySpring.config.db.DatabaseClearExtension;
import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Reply;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.repository.ReplyRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.request.ReplyCreateRequest;
import java.util.stream.LongStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
class ReplyServiceTest {
    @Autowired
    private ReplyService replyService;
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
    @DisplayName("페이징으로 게시글의 댓글들 조회 테스트")
    void findAllRepliesByTest() {
        // given
        var replies = LongStream.rangeClosed(1, 20)
                .mapToObj(index -> {
                    Reply reply = Reply.of("내용입니다." + index);
                    reply.setMember(member);
                    reply.setBoard(board);
                    return reply;
                })
                .toList();
        replyRepository.saveAll(replies);

        var pageRequest = PageRequest.of(0, 5);
        var boardId = board.getId();

        // when
        var actual = replyService.findAllRepliesBy(boardId, pageRequest);

        // then
        assertAll(() -> {
            assertEquals(replies.size() / pageRequest.getPageSize(), actual.getTotalPages());
            assertEquals(replies.size(), actual.getTotalElements());
            assertEquals(pageRequest.getPageNumber(), actual.getPageNumber());
            assertEquals(pageRequest.getPageSize(), actual.getPageSize());
            assertEquals(pageRequest.getPageSize(), actual.getRepliesResponse().size());
        });
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    @Transactional(readOnly = true)
    void createBoardTest() {
        // given
        var boardId = board.getId();

        var request = ReplyCreateRequest.builder()
                .content("내용입니다.")
                .build();

        // when
        replyService.createReply(boardId, member, request);

        // then
        var actual = replyRepository.findById(1L).get();
        assertAll(() -> {
            assertEquals(request.getContent(), actual.getContent());
            assertEquals(member, actual.getMember());
            assertEquals(board, actual.getBoard());
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글 생성 테스트")
    void createBoardByNotExistsBoardTest() {
        // given
        var boardId = 2L;

        var request = ReplyCreateRequest.builder()
                .content("내용입니다.")
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> replyService.createReply(boardId, member, request));
    }
}