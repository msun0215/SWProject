package com.example.BoardDBRestAPIBySpring.repository;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.BoardDBRestAPIBySpring.config.db.DatabaseClearExtension;
import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Reply;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(DatabaseClearExtension.class)
class ReplyRepositoryTest {
    @Autowired
    private ReplyRepository replyRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private RoleRepository roleRepository;

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
    @DisplayName("댓글 생성 테스트")
    void saveBoardTest() {
        // given
        var board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);

        String content = "댓글 내용입니다.";
        var reply = Reply.of(content);
        reply.setBoard(board);
        reply.setMember(member);

        // when
        replyRepository.save(reply);

        // then
        assertAll(() -> {
            assertEquals(1L, reply.getId());
            assertEquals(content, reply.getContent());
            assertEquals(member, reply.getMember());
            assertEquals(board, reply.getBoard());
        });
    }
}