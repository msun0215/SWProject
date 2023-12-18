package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.RoleName;
import com.example.BoardDBRestAPIBySpring.dto.PostDeleteDto;
import com.example.BoardDBRestAPIBySpring.dto.PostEditDto;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.request.PostCreateRequest;
import com.example.BoardDBRestAPIBySpring.request.PostRoleChangeRequest;
import com.example.BoardDBRestAPIBySpring.response.PostResponse;
import com.example.BoardDBRestAPIBySpring.response.PostsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

	private final PostRepository postRepository;

	@Transactional(readOnly = true)
	public PostsResponse findAllPostsBy(Pageable pageable) {
		Sort sort = Sort.by("id").descending();
		PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Page<Board> result = postRepository.findAll(pageRequest);
		return PostsResponse.of(result);
	}

	@Transactional(readOnly = true)
	public PostResponse findById(final Long id) {
		Board board = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
		return new PostResponse(board);
	}

	public void createBoard(final Member member, final PostCreateRequest request) {
		Board board = request.toEntity();
		board.setMember(member);

		postRepository.save(board);
	}

	@Transactional
	public void editBoard(final PostEditDto dto) {
		Board board = postRepository.findById(dto.getBoardId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
		Member member = dto.getMember();
		if (member.isNotOwnerFor(board)) {
			throw new IllegalArgumentException("게시글 작성자가 아닙니다.");
		}

		board.edit(dto.getRequest());
	}

	@Transactional
	public void deleteBoard(final PostDeleteDto dto) {
		Board board = postRepository.findById(dto.getBoardId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
		Member member = dto.getMember();
		if (member.hasNotDeletePermissionFor(board)) {
			throw new IllegalArgumentException("게시글 삭제 권한이 없습니다.");
		}

		postRepository.delete(board);
	}

	public void createRoleChangeBoard(final Member member, final PostRoleChangeRequest request) {
		if (member.isSameRole(request.getChangeRole())) {
			throw new IllegalArgumentException("같은 권한으로 바꿀 수 없습니다.");
		}
		RoleName currentRoleName = RoleName.findBy(member.getRoleName());
		RoleName changeRoleName = RoleName.findBy(request.getChangeRole());
		String title = String.format("[권한 변경] %s -> %s", currentRoleName, changeRoleName);
		String content = String.format("%s의 현재 권한 %s을 %s로 변경하고 싶습니다.", member.getMemberName(), currentRoleName,
				changeRoleName);
		Board board = Board.from(title, content);
		board.setMember(member);

		postRepository.save(board);
	}
}

