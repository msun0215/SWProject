package com.example.BoardDBRestAPIBySpring.response;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostResponse {

	private final Long board_no;
	private final String title;
	private final String text;
	private final int count;
	private final int del_flg;
	private final LocalDate reg_dtm;
	private final LocalDate mod_dtm;

	public PostResponse(final Board board) {
		this.board_no = board.getBoard_no();
		this.title = board.getTitle();
		this.text = board.getText();
		this.count = board.getCount();
		this.del_flg = board.getDel_flg();
		this.reg_dtm = board.getReg_dtm();
		this.mod_dtm = board.getMod_dtm();
	}

	@Builder
	public PostResponse(final Long board_no, final String title, final String text, final int count,
		final int del_flg,
		final LocalDate reg_dtm, final LocalDate mod_dtm) {
		this.board_no = board_no;
		this.title = title;
		this.text = text;
		this.count = count;
		this.del_flg = del_flg;
		this.reg_dtm = reg_dtm;
		this.mod_dtm = mod_dtm;
	}
}
