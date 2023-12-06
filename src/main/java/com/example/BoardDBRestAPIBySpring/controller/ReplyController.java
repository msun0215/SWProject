package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.request.ReplyCreateRequest;
import com.example.BoardDBRestAPIBySpring.response.RepliesResponse;
import com.example.BoardDBRestAPIBySpring.service.ReplyService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping
    public ResponseEntity<Void> createReply(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                            @PathVariable final long boardId,
                                            @RequestBody final ReplyCreateRequest request) {

        Member member = principalDetails.getMember();
        replyService.createReply(boardId, member, request);

        return ResponseEntity.created(URI.create("/boards/{boardId}/replies")).build();
    }

}
