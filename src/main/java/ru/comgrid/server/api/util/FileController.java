package ru.comgrid.server.api.util;

import lombok.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import ru.comgrid.server.exception.ImageEmptyParamException;
import ru.comgrid.server.exception.ImageWriteToFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Controller
@RequestMapping(value = "/file", produces = "application/json; charset=utf-8")
public class FileController{
	public final String FILE_ROUTE;

	public FileController(@Value("${ru.comgrid.server.api.util.file-controller.image-path}") String FILE_ROUTE){
		this.FILE_ROUTE = FILE_ROUTE;
		new File(FILE_ROUTE).mkdirs();
	}

	public ImageEntity uploadImage(@RequestParam("file") MultipartFile multipartFile){
		if(!multipartFile.isEmpty()){
			String imageUrl = getImageUrl(multipartFile);
			try {
				byte[] bytes = multipartFile.getBytes();
				Files.write(Path.of(imageUrl), bytes);
				return new ImageEntity(imageUrl);
			} catch (IOException e) {
				e.printStackTrace();
				throw new ImageWriteToFileException();
			}
		}else{
			throw new ImageEmptyParamException();
		}
	}

	public String getImageUrl(MultipartFile file){
		return FILE_ROUTE + UUID.randomUUID() + "." + FilenameUtils.getExtension(file.getOriginalFilename());
	}


	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public static class ImageEntity{
		private String url;
	}
}
