package ru.comgrid.server.api.util;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import ru.comgrid.server.exception.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.util.StringUtils.getFilenameExtension;

@Controller
@RequestMapping(value = "/images")
public class FileController{
	public final String fileRoute;
	private final ImageService imageService;
	private final RestTemplate restTemplate = new RestTemplate();
	private final Set<String> allowedExtensions;

	public FileController(
		@Value("${ru.comgrid.server.file-controller.image-path}") String fileRoute,
		@Value("${ru.comgrid.server.image-compressor.allowed-extensions}") List<String> allowedExtensions,
		@Autowired ImageService imageService
	) throws IOException{
		this.fileRoute = fileRoute;
		this.allowedExtensions = new HashSet<>(allowedExtensions);
		this.imageService = imageService;
		Files.createDirectories(Path.of(fileRoute));
	}

	/**
	 * Uploads file it is not yet uploaded and returns link to it.
	 * Either file or fileLink has to be specified.
	 * @param file uploaded file or null
	 * @param fileLink link to file or null
	 * @return relative link to file
	 */
	public ImageEntity uploadImage(@Nullable MultipartFile file, @Nullable String fileLink){
		String imagePath = getNewImageUrl();
		try {
			if(file == null || file.isEmpty()){
				return getImageFromLink(fileLink, imagePath);
			}
			byte[] bytes = imageService.compressImage(file.getBytes());
			Files.write(Path.of(imagePath), bytes);
			return new ImageEntity(imagePath);
		} catch (IOException e) {
			throw new ImageWriteToFileException();
		}
	}

	@GetMapping("/{fileLink}")
	public ResponseEntity<byte[]> getFile(@PathVariable String fileLink){
		fileLink = fileRoute + fileLink;
		if(!fileLink.startsWith(fileRoute))
			throw new InvalidLinkException(fileLink, new Throwable(Path.of("/" + fileLink).toAbsolutePath() + "_fileRoute"));

		try{
			String filenameExtension = getFilenameExtension(fileLink);
			if(filenameExtension == null)
				throw new InvalidLinkException(fileLink + ", no extension");
			if(!allowedExtensions.contains(filenameExtension))
				throw new InvalidLinkException(fileLink + ", bad extension");
			//check whether it's legal uuid string
			UUID.fromString(fileLink.substring(fileRoute.length(), fileLink.length() - filenameExtension.length() - 1));
			System.out.println(Path.of("/" + fileLink).toAbsolutePath());
			return ResponseEntity
				.ok()
				.contentType(new MediaType("image", filenameExtension))
				.body(Files.readAllBytes(Path.of(fileLink)));
		}catch(IllegalArgumentException | NullPointerException | IOException | StringIndexOutOfBoundsException e){
			throw new InvalidLinkException(fileLink, e);
		}
	}

	@NotNull
	private ImageEntity getImageFromLink(String fileLink, String imagePath) throws IOException{
		if(fileLink == null){
			throw new ImageEmptyParamException();
		}
		if(fileLink.startsWith("/images/")){
			checkFileExists(fileLink);
			return new ImageEntity(fileLink);
		}
		byte[] bytes = imageService.compressImage(fileLink);
		Files.write(Path.of(imagePath), bytes);
		return new ImageEntity(imagePath);
	}

	private void checkFileExists(String fileLink){
		if(!Files.exists(Path.of(fileLink), LinkOption.NOFOLLOW_LINKS))
			throw new ImageDoesNotExistException(fileLink);
	}

	private String getNewImageUrl(){
		return fileRoute + UUID.randomUUID() + ".webp";
	}


	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	public static class ImageEntity{
		private String url;
	}
}
