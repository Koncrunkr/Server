package ru.comgrid.server.api.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.comgrid.server.exception.InvalidLinkException;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
		try{
			return restTemplate.getForObject(imageCompressor.toString() + "&src=" + URLEncoder.encode(imageLink, StandardCharsets.UTF_8), byte[].class);
		}catch(HttpClientErrorException.BadRequest e){
			throw new InvalidLinkException();
		}
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
