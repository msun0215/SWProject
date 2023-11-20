package com.example.BoardDBRestAPIBySpring.service;

import com.example.BoardDBRestAPIBySpring.domain.Board;
import com.example.BoardDBRestAPIBySpring.domain.PostFile;
import com.example.BoardDBRestAPIBySpring.repository.PostFileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostFileService {
	private static final String DIRECTORY = "C:\\Users\\admin\\Desktop\\SpringWorkspace\\BoardDBRestAPIBySpring\\files\\post\\";

	private final PostFileRepository postFileRepository;
	private final FileService fileService;
	// TODO: 2023-10-04 프로젝트 내에 첨부파일을 저장하는 기능 구현하기
	// https://velog.io/@mooh2jj/SpringBoot-File-uploaddownload-%EA%B5%AC%ED%98%84
	// https://junhyunny.github.io/spring-boot/vue.js/multipartfile-in-dto/

	public void save(final Board board, final List<MultipartFile> files) {
		log.info("files.size() = {}", files.size());
		for (MultipartFile file : files) {
			PostFile postFile = PostFile.builder()
				.name(generateRandomFileName(file))
				.path(DIRECTORY)
				.build();
			postFile.setBoard(board);
			postFileRepository.save(postFile);
			fileService.save(file, postFile.getAbsolutePath());
		}
	}

	private String generateRandomFileName(final MultipartFile file) {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().concat("_")
			.concat(Objects.requireNonNull(file.getOriginalFilename()));
	}

	public byte[] getFileByFileName(final String name) throws IOException {
		PostFile postFile = postFileRepository.findByName(name)
			.orElseThrow(() -> new IllegalArgumentException(name + " 파일이 존재하지 않습니다."));
		String path = postFile.getAbsolutePath();

		return Files.readAllBytes(new File(path).toPath());
	}

	public List<byte[]> getPostFilesByBoardNo(final Long board_no) {
		List<PostFile> postFilesByBoardNo = postFileRepository.findPostFilesByBoardNo(board_no);

		return postFilesByBoardNo.stream()
			.map(PostFile::getAbsolutePath)
			.map(path -> {
				try {
					return Files.readAllBytes(new File(path).toPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}).toList();
	}
}
