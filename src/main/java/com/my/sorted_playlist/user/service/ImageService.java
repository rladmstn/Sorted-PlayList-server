package com.my.sorted_playlist.user.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ImageService {
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	public String saveImage(MultipartFile multipartFile){
		if(multipartFile == null)
			return null;
		String originalImageName = multipartFile.getOriginalFilename();
		String fileName = UUID.randomUUID().toString().concat(Objects.requireNonNull(originalImageName));
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		try(InputStream inputStream = multipartFile.getInputStream()){
			amazonS3.putObject(bucket, fileName, inputStream, metadata);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return amazonS3.getUrl(bucket, fileName).toString();
	}

	public void deleteImage(String originalImageName){
		String splitStr = ".com/";
		String fileName = originalImageName.substring(originalImageName.lastIndexOf(splitStr) + splitStr.length());
		String file = URLDecoder.decode(fileName, StandardCharsets.UTF_8);
		amazonS3.deleteObject(bucket, file);
	}
}
