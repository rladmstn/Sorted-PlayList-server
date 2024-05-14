package com.my.sorted_playlist.user.controller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my.sorted_playlist.user.dto.RegisterRequestDTO;
import com.my.sorted_playlist.common.exception.RequestException;
import com.my.sorted_playlist.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "유저 컨트롤러", description = "회원과 관련된 API 명세서")
public class UserController {
	private final UserService userService;

	@PostMapping(value = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "회원가입", description = "소셜 로그인을 위해 회원가입 할 때 사용하는 API")
	public ResponseEntity<Object> register(@Valid @RequestPart RegisterRequestDTO registerRequestDTO,
		Errors errors, @RequestPart(required = false) MultipartFile profileImage){
		if(errors.hasErrors())
			throw new RequestException("회원가입의 입력이 올바르지 않습니다", errors);
		userService.register(registerRequestDTO, profileImage);
		return ResponseEntity.ok().body("회원가입 성공");
	}

	@PostMapping("/email-check")
	@Operation(summary = "이메일 중복 확인", description = "회원가입 시, 이메일 중복 확인 버튼을 누를 때 사용하는 API")
	public ResponseEntity<Object> validateDuplicatedEmail(@RequestParam String email){
		userService.validateDuplicatedEmail(email);
		return ResponseEntity.ok().body("OK");
	}
}
