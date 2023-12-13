package com.example.BoardDBRestAPIBySpring.controller;

import com.example.BoardDBRestAPIBySpring.config.auth.PrincipalDetails;
import com.example.BoardDBRestAPIBySpring.domain.Member;
import com.example.BoardDBRestAPIBySpring.domain.Reply;
import com.example.BoardDBRestAPIBySpring.dto.ReplyDeletetDto;
import com.example.BoardDBRestAPIBySpring.request.ReplyCreateRequest;
import com.example.BoardDBRestAPIBySpring.request.ReplyEditDto;
import com.example.BoardDBRestAPIBySpring.request.ReplyEditRequest;
import com.example.BoardDBRestAPIBySpring.response.RepliesResponse;
import com.example.BoardDBRestAPIBySpring.response.ReplyResponse;
import com.example.BoardDBRestAPIBySpring.service.ReplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<ReplyResponse> createReply(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                            @PathVariable final long boardId,
                                            @RequestBody final ReplyCreateRequest request) {

        Member member = principalDetails.getMember();
        Reply reply = replyService.createReply(boardId, member, request);

        ReplyResponse replyResponse = ReplyResponse.of(reply);

        return ResponseEntity.status(201).body(replyResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> editReply(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                          @PathVariable final long boardId, @PathVariable final long id,
                                          @RequestBody final ReplyEditRequest request) {

        Member member = principalDetails.getMember();
        ReplyEditDto dto = ReplyEditDto.builder()
                .replyId(id)
                .boardId(boardId)
                .member(member)
                .request(request)
                .build();
        replyService.editReply(dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReply(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                            @PathVariable final long boardId, @PathVariable Long id) {
        Member member = principalDetails.getMember();
        ReplyDeletetDto dto = ReplyDeletetDto.builder()
                .replyId(id)
                .boardId(boardId)
                .member(member)
                .build();
        replyService.deleteReply(dto);

        return ResponseEntity.ok().build();
    }
}
