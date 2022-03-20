package ru.comgrid.server.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageService{
	private final HttpHeaders headers = new HttpHeaders();
	private final RestTemplate restTemplate = new RestTemplate();
	private final URI imageCompressor;

	public ImageService(@Value("${ru.comgrid.server.image-compressor}") String imageCompressor){
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		this.imageCompressor = URI.create(imageCompressor);
	}

	public final byte[] compressImage(String imageLink){
		return restTemplate.getForObject(imageCompressor.toString() + "&src=" + imageLink, byte[].class);
	}

	public final byte[] compressImage(byte[] image){
		var body = new LinkedMultiValueMap<String, Object>();
		body.add("file", new TrueByteArrayResource(image));

		var requestEntity = new HttpEntity<>(body, headers);
		return restTemplate.postForObject(imageCompressor, requestEntity, byte[].class);
	}

	private static class TrueByteArrayResource extends ByteArrayResource{
		public TrueByteArrayResource(byte[] file){
			super(file);
		}

		@Override
		public String getFilename(){
			return "file";
		}
	}
}
