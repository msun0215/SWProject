package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.response.RepliesResponse;
import com.example.BoardDBRestAPIBySpring.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/replies")
@Slf4j
public class ReplyController {

    private final ReplyService replyService;

    @GetMapping
    public RepliesResponse getAllRepliesByBoardId(@PathVariable final long boardId,
                                                  @PageableDefault Pageable pageable) {

        return replyService.findAllRepliesBy(boardId, pageable);
    }
}