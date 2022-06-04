package ru.comgrid.server.controller.util;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.comgrid.server.service.file.FileService;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/images")
public class FileController{
	public final String fileRoute;
	private final FileService fileService;

	public FileController(@Value("${ru.comgrid.images.fileRoute}") String fileRoute, FileService fileService){
		this.fileRoute = fileRoute;
		this.fileService = fileService;
	}

	@ResponseBody
	@ApiResponse(responseCode = "500", description = "image.uploading_error. Internal server error whilst uploading image")
	@ApiResponse(responseCode = "400", description = "image.empty. Provided image was empty.")
	@ApiResponse(responseCode = "400", description = "link.invalid. Provided link for image was invalid.")
	@PostMapping("/upload")
	public ImageEntity uploadImage(
		@ModelAttribute @Valid Image file
	){
		return fileService.uploadImage(file);
	}

	@ApiResponse(responseCode = "422", description = "link.invalid. Provided link was invalid. See details.")
	@GetMapping("/get/{fileLink}")
	public ResponseEntity<byte[]> getFile(@PathVariable String fileLink){
		fileLink = fileRoute + "get/" + fileLink;

		return fileService.getFile(fileLink);
	}
}
