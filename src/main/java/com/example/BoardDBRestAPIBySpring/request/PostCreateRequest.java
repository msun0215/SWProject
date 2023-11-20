package com.example.BoardDBRestAPIBySpring.request;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class PostCreateRequest {

	@NotBlank(message = "작성자 이름은 필수입니다.")
	private final String mem_id;

	@NotBlank(message = "제목은 필수입니다.")
	private final String title;

	@NotBlank(message = "내용은 필수입니다.")
	private final String text;

	private List<MultipartFile> files = new ArrayList<>();

	@Builder
	public PostCreateRequest(final String mem_id, final String title, final String text) {
		this.mem_id = mem_id;
		this.title = title;
		this.text = text;
	}

	public Board toEntity() {
		return Board.builder()
			.title(title)
			.text(text)
			.count(0)
			.del_flg(0)
			.reg_dtm(LocalDate.now())
			.mod_dtm(LocalDate.now())
			.mem_id(mem_id)
			.build();
	}
}
