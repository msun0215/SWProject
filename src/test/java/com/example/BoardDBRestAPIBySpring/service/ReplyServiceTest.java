package com.example.BoardDBRestAPIBySpring.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.BoardDBRestAPIBySpring.config.db.DatabaseClearExtension;
import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Reply;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.dto.ReplyDeletetDto;
import com.example.BoardDBRestAPIBySpring.dto.ReplyEditDto;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.repository.ReplyRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.request.ReplyCreateRequest;
import com.example.BoardDBRestAPIBySpring.request.ReplyEditRequest;
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
    private Role role;
    private Member admin;

    @BeforeEach
    void init() {
        var memberID = "test@test.com";
        var memberPW = "test";

        member = new Member();
        member.setMemberID(memberID);
        member.setMemberPW(bCryptPasswordEncoder.encode(memberPW));
        member.setMemberName("test");
        member.setMemberNickname("testk");
        role = new Role();
        role.setRoleName("USER");
        roleRepository.save(role);
        member.setRoles(role);

        memberRepository.save(member);

        board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);

        admin = new Member();
        admin.setMemberID("admin@admin.com");
        admin.setMemberPW(bCryptPasswordEncoder.encode(memberPW));
        admin.setMemberName("admin");
        admin.setMemberNickname("adminK");
        var adminRole = new Role();
        adminRole.setRoleName("ADMIN");
        roleRepository.save(adminRole);
        admin.setRoles(adminRole);
        memberRepository.save(admin);
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

    @Test
    @DisplayName("댓글 수정 테스트")
    @Transactional(readOnly = true)
    void editReplyTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var request = ReplyEditRequest.builder()
                .content("수정된 내용입니다.")
                .build();

        var replyId = reply.getId();

        var dto = ReplyEditDto.builder()
                .replyId(replyId)
                .boardId(board.getId())
                .member(member)
                .request(request)
                .build();

        // when
        replyService.editReply(dto);

        // then
        var actual = replyRepository.findById(replyId).get();
        assertAll(() -> {
            assertEquals(request.getContent(), actual.getContent());
            assertEquals(board, actual.getBoard());
            assertEquals(member, actual.getMember());
        });
    }

    @Test
    @DisplayName("존재하지 않는 댓글 수정 테스트")
    void editReplyByNotExistTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var request = ReplyEditRequest.builder()
                .content("수정된 내용입니다.")
                .build();

        var dto = ReplyEditDto.builder()
                .replyId(2L)
                .boardId(board.getId())
                .member(member)
                .request(request)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> replyService.editReply(dto));
    }

    @Test
    @DisplayName("게시글의 댓글이 아닌 댓글 수정 테스트")
    void editReplyByNotBoardReplyTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var request = ReplyEditRequest.builder()
                .content("수정된 내용입니다.")
                .build();

        var replyId = reply.getId();

        var dto = ReplyEditDto.builder()
                .replyId(replyId)
                .boardId(2L)
                .member(member)
                .request(request)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> replyService.editReply(dto));
    }

    @Test
    @DisplayName("작성자가 아닌 댓글 수정 테스트")
    void editBoardByNotOwnerTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var request = ReplyEditRequest.builder()
                .content("수정된 내용입니다.")
                .build();

        var otherMember = new Member();
        otherMember.setMemberID("other@other.com");
        otherMember.setMemberPW(bCryptPasswordEncoder.encode("other"));
        otherMember.setMemberName("other");
        otherMember.setMemberNickname("otherk");
        otherMember.setRoles(role);

        memberRepository.save(otherMember);

        var dto = ReplyEditDto.builder()
                .replyId(reply.getId())
                .boardId(board.getId())
                .member(otherMember)
                .request(request)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> replyService.editReply(dto));
    }

    @Test
    @DisplayName("어드민 댓글 수정 테스트")
    @Transactional(readOnly = true)
    void editBoardByAdminTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var replyId = reply.getId();

        var request = ReplyEditRequest.builder()
                .content("수정된 내용입니다.")
                .build();

        var dto = ReplyEditDto.builder()
                .replyId(reply.getId())
                .boardId(board.getId())
                .member(admin)
                .request(request)
                .build();

        // when
        replyService.editReply(dto);

        // then
        var actual = replyRepository.findById(replyId).get();
        assertAll(() -> {
            assertEquals(request.getContent(), actual.getContent());
            assertEquals(board, actual.getBoard());
            assertEquals(member, actual.getMember());
        });
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    @Transactional(readOnly = true)
    void deleteReplyTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var replyId = reply.getId();

        var dto = ReplyDeletetDto.builder()
                .replyId(replyId)
                .boardId(board.getId())
                .member(member)
                .build();

        // when
        replyService.deleteReply(dto);

        // then
        var actual = replyRepository.findById(replyId).isEmpty();
        assertTrue(actual);
    }

    @Test
    @DisplayName("존재하지 않는 댓글 삭제 테스트")
    void deleteReplyByNotExistTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var dto = ReplyDeletetDto.builder()
                .replyId(2L)
                .boardId(board.getId())
                .member(member)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> replyService.deleteReply(dto));
    }

    @Test
    @DisplayName("게시글의 댓글이 아닌 댓글 삭제 테스트")
    void deleteReplyByNotBoardReplyTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var replyId = reply.getId();

        var dto = ReplyDeletetDto.builder()
                .replyId(replyId)
                .boardId(2L)
                .member(member)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> replyService.deleteReply(dto));
    }

    @Test
    @DisplayName("작성자가 아닌 댓글 삭제 테스트")
    void deleteBoardByNotOwnerTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var otherMember = new Member();
        otherMember.setMemberID("other@other.com");
        otherMember.setMemberPW(bCryptPasswordEncoder.encode("other"));
        otherMember.setMemberName("other");
        otherMember.setMemberNickname("otherk");
        otherMember.setRoles(role);

        memberRepository.save(otherMember);

        var dto = ReplyDeletetDto.builder()
                .replyId(reply.getId())
                .boardId(board.getId())
                .member(otherMember)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> replyService.deleteReply(dto));
    }

    @Test
    @DisplayName("어드민 댓글 삭제 테스트")
    void deleteReplyByAdminTest() {
        // given
        var reply = Reply.of("내용입니다.");
        reply.setBoard(board);
        reply.setMember(member);
        replyRepository.save(reply);

        var replyId = reply.getId();

        var dto = ReplyDeletetDto.builder()
                .replyId(replyId)
                .boardId(board.getId())
                .member(admin)
                .build();

        // when
        replyService.deleteReply(dto);

        // then
        var actual = replyRepository.findById(replyId).isEmpty();
        assertTrue(actual);
    }
}