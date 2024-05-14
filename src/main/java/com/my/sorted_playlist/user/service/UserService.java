package com.my.sorted_playlist.user.service;

import java.io.IOException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.my.sorted_playlist.common.enums.Role;
import com.my.sorted_playlist.user.dto.RegisterRequestDTO;
import com.my.sorted_playlist.user.exception.UserValidationException;
import com.my.sorted_playlist.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageService imageService;

	public void register(RegisterRequestDTO registerRequestDTO, MultipartFile profileImage) {
		try {
			validateDuplicatedEmail(registerRequestDTO.email());
			String imageUrl = imageService.saveImage(profileImage);

			userRepository.save(registerRequestDTO.toEntity(
				registerRequestDTO.email(),
				passwordEncoder.encode(registerRequestDTO.password()),
				registerRequestDTO.nickname(),
				imageUrl,
				Role.USER
			));
			log.info("success to register");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void validateDuplicatedEmail(String email){
		if(userRepository.existsByEmail(email))
			throw new UserValidationException("이미 가입된 이메일 입니다.");
	}

}
