package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Reply;
import com.example.BoardDBRestAPIBySpring.dto.ReplyDeletetDto;
import com.example.BoardDBRestAPIBySpring.dto.ReplyEditDto;
import com.example.BoardDBRestAPIBySpring.repository.PostRepository;
import com.example.BoardDBRestAPIBySpring.repository.ReplyRepository;
import com.example.BoardDBRestAPIBySpring.request.ReplyCreateRequest;
import com.example.BoardDBRestAPIBySpring.response.RepliesResponse;
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
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public RepliesResponse findAllRepliesBy(final long boardId, final Pageable pageable) {
        Sort sort = Sort.by("id").descending();
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Reply> replies = replyRepository.findAllByBoardId(boardId, pageRequest);
        return RepliesResponse.of(replies);
    }

    @Transactional
    public Reply createReply(final long boardId, final Member member, final ReplyCreateRequest request) {
        Board board = postRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        Reply reply = request.toEntity();
        reply.setBoard(board);
        reply.setMember(member);

        return replyRepository.save(reply);
    }

    @Transactional
    public void editReply(final ReplyEditDto dto) {
        Reply reply = replyRepository.findById(dto.getReplyId())
                .orElseThrow(() -> new IllegalArgumentException("존자해지 않는 댓글입니다."));
        if (reply.isNotSameBoardId(dto.getBoardId())) {
            throw new IllegalArgumentException("게시글의 댓글이 아닙니다.");
        }

        Member member = dto.getMember();
        if (member.hasNotUpdatePermissionFor(reply)) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }

        reply.edit(dto);
    }

    @Transactional
    public void deleteReply(final ReplyDeletetDto dto) {
        Reply reply = replyRepository.findById(dto.getReplyId())
                .orElseThrow(() -> new IllegalArgumentException("존자해지 않는 댓글입니다."));
        if (reply.isNotSameBoardId(dto.getBoardId())) {
            throw new IllegalArgumentException("게시글의 댓글이 아닙니다.");
        }

        Member member = dto.getMember();
        if (member.hasNotUpdatePermissionFor(reply)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }

        replyRepository.delete(reply);
    }
}
