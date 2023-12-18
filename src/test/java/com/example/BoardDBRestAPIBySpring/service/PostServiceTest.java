package com.example.BoardDBRestAPIBySpring.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.BoardDBRestAPIBySpring.config.db.DatabaseClearExtension;
import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Role;
import com.example.BoardDBRestAPIBySpring.domain.RoleName;
import com.example.BoardDBRestAPIBySpring.dto.PostDeleteDto;
import com.example.BoardDBRestAPIBySpring.dto.PostEditDto;
import com.example.BoardDBRestAPIBySpring.repository.MemberRepository;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.repository.RoleRepository;
import com.example.BoardDBRestAPIBySpring.request.PostCreateRequest;
import com.example.BoardDBRestAPIBySpring.request.PostEditRequest;
import com.example.BoardDBRestAPIBySpring.request.PostRoleChangeRequest;
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
class PostServiceTest {
	@Autowired
	private PostService postService;
    @Autowired
    private PostRepository postRepository;
	@Autowired
	private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private Member member;
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
    @DisplayName("페이징으로 게시글들 조회 테스트")
	void findAllPostsByTest() {
		// given
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
            assertEquals(pageRequest.getPageSize(), actual.getPostsResponse().size());
        });
	}

    @Test
    @DisplayName("게시글 단건 조회 테스트")
    void findPostTest() {
        // given
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

    @Test
    @DisplayName("게시글 생성 테스트")
    @Transactional(readOnly = true)
    void createBoardTest() {
        // given
        var request = PostCreateRequest.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        // when
        postService.createBoard(member, request);

        // then
        var actual = postRepository.findById(1L).get();
        assertAll(() -> {
            assertEquals(request.getTitle(), actual.getTitle());
            assertEquals(request.getContent(), actual.getContent());
            assertEquals(member, actual.getMember());
        });
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @Transactional(readOnly = true)
    void editBoardTest() {
        // given
        var boards = LongStream.rangeClosed(1, 20)
                .mapToObj(index -> {
                    Board board = Board.from("제목입니다." + index, "내용입니다." + index);
                    board.setMember(member);
                    return board;
                })
                .toList();
        postRepository.saveAll(boards);

        var request = PostEditRequest.builder()
                .title("수정된 제목입니다.")
                .content("수정된 내용입니다.")
                .build();

        var boardId = 1L;

        var dto = PostEditDto.builder()
                .boardId(boardId)
                .member(member)
                .request(request)
                .build();

        // when
        postService.editBoard(dto);

        // then
        var actual = postRepository.findById(boardId).get();
        assertAll(() -> {
            assertEquals(boardId, actual.getId());
            assertEquals(request.getTitle(), actual.getTitle());
            assertEquals(request.getContent(), actual.getContent());
            assertEquals(member, actual.getMember());
        });
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정 테스트")
    void editBoardByNotExistTest() {
        // given
        var board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);

        var request = PostEditRequest.builder()
                .title("수정된 제목입니다.")
                .content("수정된 내용입니다.")
                .build();

        var boardId = 2L;

        var dto = PostEditDto.builder()
                .boardId(boardId)
                .member(member)
                .request(request)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> postService.editBoard(dto));
    }

    @Test
    @DisplayName("작성자가 아닌 게시글 수정 테스트")
    void editBoardByNotOwnerTest() {
        // given
        var board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);

        var otherMember = new Member();
        otherMember.setMemberID("other@other.com");
        otherMember.setMemberPW(bCryptPasswordEncoder.encode("other"));
        otherMember.setMemberName("other");
        otherMember.setMemberNickname("otherk");
        otherMember.setRoles(role);

        memberRepository.save(otherMember);

        var request = PostEditRequest.builder()
                .title("수정된 제목입니다.")
                .content("수정된 내용입니다.")
                .build();

        var boardId = 1L;

        var dto = PostEditDto.builder()
                .boardId(boardId)
                .member(otherMember)
                .request(request)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> postService.editBoard(dto));
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    void deleteBoardTest() {
        // given
        var board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);

        var boardId = 1L;

        var dto = PostDeleteDto.builder()
                .boardId(boardId)
                .member(member)
                .build();

        // when
        postService.deleteBoard(dto);

        // then
        var actual = postRepository.findById(boardId).isEmpty();
        assertTrue(actual);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 삭제 테스트")
    void deleteBoardByNotExistTest() {
        // given
        var board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);

        var boardId = 2L;

        var dto = PostDeleteDto.builder()
                .boardId(boardId)
                .member(member)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> postService.deleteBoard(dto));
    }

    @Test
    @DisplayName("작성자가 아닌 게시글 삭제 테스트")
    void deleteBoardByNotOwnerTest() {
        // given
        var board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);

        var otherMember = new Member();
        otherMember.setMemberID("other@other.com");
        otherMember.setMemberPW(bCryptPasswordEncoder.encode("other"));
        otherMember.setMemberName("other");
        otherMember.setMemberNickname("otherk");
        otherMember.setRoles(role);

        memberRepository.save(otherMember);

        var boardId = 1L;

        var dto = PostDeleteDto.builder()
                .boardId(boardId)
                .member(otherMember)
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> postService.deleteBoard(dto));
    }

    @Test
    @DisplayName("어드민 게시글 삭제 테스트")
    void deleteBoardByAdminTest() {
        // given
        var board = Board.from("제목입니다.", "내용입니다.");
        board.setMember(member);
        postRepository.save(board);

        var boardId = 1L;

        var dto = PostDeleteDto.builder()
                .boardId(boardId)
                .member(admin)
                .build();

        // when
        postService.deleteBoard(dto);

        // then
        var actual = postRepository.findById(boardId).isEmpty();
        assertTrue(actual);
    }

    @Test
    @DisplayName("권한 변경 게시글 생성 테스트")
    void createRoleChangeBoardTest() {
        // given
        var request = PostRoleChangeRequest.builder()
                .changeRole(RoleName.MANAGER.toString())
                .build();

        // when
        postService.createRoleChangeBoard(member, request);

        // then
        var actual = postRepository.findById(1L).get();
        assertTrue(actual.getTitle().contains("[권한 변경]"));
    }

    @Test
    @DisplayName("같은 권한으로 변경 게시글 생성 테스트")
    void createSameRoleChangeBoardTest() {
        // given
        var request = PostRoleChangeRequest.builder()
                .changeRole(member.getRoleName())
                .build();

        // expected
        assertThrows(IllegalArgumentException.class, () -> postService.createRoleChangeBoard(member, request));
    }
}