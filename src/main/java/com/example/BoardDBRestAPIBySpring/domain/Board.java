package com.example.BoardDBRestAPIBySpring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_no", updatable = false)
	private Long board_no;
	@Column(nullable = false)
	private String title;
	@Lob
	@Column(nullable = false)
	private String text;
	private int count;
	private int del_flg;
	private LocalDate reg_dtm;
	private LocalDate mod_dtm;
	private String mem_id;

	@ManyToOne
	@JoinColumn(name = "member_id")
	private Member member;

	@Builder
	public Board(final String title, final String text, final int count, final int del_flg,
		final LocalDate reg_dtm, final LocalDate mod_dtm, final String mem_id) {
		this.title = title;
		this.text = text;
		this.count = count;
		this.del_flg = del_flg;
		this.reg_dtm = reg_dtm;
		this.mod_dtm = mod_dtm;
		this.mem_id = mem_id;
	}

	// 연관관계 편의 메서드
	public void setMember(final Member member) {
		this.member = member;
	}
}
