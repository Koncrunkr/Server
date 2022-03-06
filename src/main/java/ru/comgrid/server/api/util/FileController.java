package ru.comgrid.server.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Controller
@RequestMapping(value = "/file", produces = "application/json; charset=utf-8")
public class FileController{


	@PostMapping("/upload")
	public ResponseEntity<ImageEntity> uploadImage(
		@RequestParam("file") MultipartFile multipartFile
	){
		String fileName = optimizeImage(multipartFile);
		return ResponseEntity.ok(new ImageEntity(fileName));
	}

	public String optimizeImage(MultipartFile file){
		return UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
	}


	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	private static class ImageEntity{
		private String url;
	}
}
