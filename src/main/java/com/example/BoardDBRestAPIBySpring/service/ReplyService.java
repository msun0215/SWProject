package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.domain.Reply;
import com.example.BoardDBRestAPIBySpring.repository.ReplyRepository;
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

    @Transactional(readOnly = true)
    public RepliesResponse findAllRepliesBy(final long boardId, final Pageable pageable) {
        Sort sort = Sort.by("id").descending();
        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Reply> replies = replyRepository.findAllByBoardId(boardId, pageRequest);
        return RepliesResponse.of(replies);
    }
}
