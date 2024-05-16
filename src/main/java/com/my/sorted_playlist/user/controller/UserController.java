package com.my.sorted_playlist.user.controller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.my.sorted_playlist.user.dto.RegisterRequest;
import com.my.sorted_playlist.common.exception.RequestException;
import com.my.sorted_playlist.user.dto.LogInRequest;
import com.my.sorted_playlist.user.dto.UserResponse;
import com.my.sorted_playlist.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
	public ResponseEntity<Object> register(@Valid @RequestPart RegisterRequest registerRequest,
		Errors errors, @RequestPart(required = false) MultipartFile profileImage){
		if(errors.hasErrors())
			throw new RequestException("회원가입의 입력이 올바르지 않습니다", errors);
		userService.register(registerRequest, profileImage);
		return ResponseEntity.ok().body("회원가입 성공");
	}

	@PostMapping("/email-check")
	@Operation(summary = "이메일 중복 확인", description = "회원가입 시, 이메일 중복 확인 버튼을 누를 때 사용하는 API")
	public ResponseEntity<Object> validateDuplicatedEmail(@RequestParam String email){
		userService.validateDuplicatedEmail(email);
		return ResponseEntity.ok().body("OK");
	}

	@PostMapping("/login")
	@Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하는 API")
	public ResponseEntity<Object> logIn(@Valid @RequestBody LogInRequest logInRequest, Errors errors,
		HttpServletRequest httpServletRequest){
		if(errors.hasErrors())
			throw new RequestException("로그인의 입력이 올바르지 않습니다.", errors);

		UserResponse user = userService.logIn(logInRequest);

		httpServletRequest.getSession().invalidate(); // 기존 세션 파기하기
		HttpSession session = httpServletRequest.getSession(true); // 세션 없으면 생성
		session.setAttribute("loginUser", user);
		session.setMaxInactiveInterval(60 * 60 * 8); // Session 8시간 유지

		return ResponseEntity.ok().body("OK");
	}

	@GetMapping("/test")
	@Operation(summary = "session test API")
	public ResponseEntity<Object> test(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		if(session == null)
			return ResponseEntity.badRequest().body("로그인 되어있지 않습니다.");
		UserResponse user = (UserResponse) session.getAttribute("loginUser");
		log.info("session test API");
		return ResponseEntity.ok().body(user.email());
	}
}
