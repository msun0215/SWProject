package com.example.BoardDBRestAPIBySpring.response;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponse {

	private final Long id;
	private final String title;
	private final String content;
	private final LocalDate createDate;
	private final LocalDate modifyDate;
	private final String memberName;
	private final String memberID;

	public PostResponse(final Board board) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.content = board.getContent();
		this.createDate = board.getCreateDate();
		this.modifyDate = board.getModifyDate();
		this.memberName = board.getMember().getMemberName();
		this.memberID = board.getMember().getMemberID();
	}

	@Builder
	public PostResponse(final Long id, final String title, final String content, final LocalDate createDate,
						final LocalDate modifyDate, final String memberName, final String memberID) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
		this.memberName = memberName;
		this.memberID = memberID;
	}
}
