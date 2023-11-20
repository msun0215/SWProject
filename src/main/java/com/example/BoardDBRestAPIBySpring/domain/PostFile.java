package com.example.BoardDBRestAPIBySpring.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 참조
// https://velog.io/@yu-jin-song/SpringBoot-%EA%B2%8C%EC%8B%9C%ED%8C%90-%EA%B5%AC%ED%98%84-4-MultipartFile-%EB%8B%A4%EC%A4%91-%ED%8C%8C%EC%9D%BC%EC%9D%B4%EB%AF%B8%EC%A7%80-%EC%97%85%EB%A1%9C%EB%93%9C
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostFile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String path;

	@ManyToOne
	@JoinColumn(name = "board_no")
	private Board board;

	@Builder
	public PostFile(final String name, final String path) {
		this.name = name;
		this.path = path;
	}

	// 연관관계 편의 메서드
	public void setBoard(final Board board) {
		this.board = board;

		if (!board.getPostFiles().contains(this)) {
			board.getPostFiles().add(this);
		}
	}

	public String getAbsolutePath() {
		return path.concat(name);
	}
}
