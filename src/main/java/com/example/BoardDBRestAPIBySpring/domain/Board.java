package com.example.BoardDBRestAPIBySpring.domain;

import static jakarta.persistence.FetchType.LAZY;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String title;
	@Lob
	@Column(nullable = false)
	private String content;
	private LocalDate createDate;
	private LocalDate modifyDate;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Builder
	public Board(final String title, final String content, final LocalDate createDate, final LocalDate modifyDate) {
		this.title = title;
		this.content = content;
		this.createDate = createDate;
		this.modifyDate = modifyDate;
	}

	public static Board from(final String title, final String content) {
		return Board.builder()
				.title(title)
				.content(content)
				.createDate(LocalDate.now())
				.modifyDate(LocalDate.now())
				.build();
	}

	// 연관관계 편의 메서드
	public void setMember(final Member member) {
		this.member = member;
	}
}
