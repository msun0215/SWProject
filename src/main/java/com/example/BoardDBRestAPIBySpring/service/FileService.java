package com.example.BoardDBRestAPIBySpring.service;

import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileService {

	public void save(final MultipartFile file, final String path) {
		try {
			file.transferTo(new File(path));
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}
