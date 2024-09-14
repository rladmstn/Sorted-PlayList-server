package com.my.sorted_playlist.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.my.sorted_playlist.common.enums.Role;
import com.my.sorted_playlist.user.domain.User;
import com.my.sorted_playlist.user.dto.RegisterRequest;
import com.my.sorted_playlist.user.dto.LogInRequest;
import com.my.sorted_playlist.user.dto.UserInfoResponse;
import com.my.sorted_playlist.user.dto.UserResponse;
import com.my.sorted_playlist.user.exception.UserPermissionException;
import com.my.sorted_playlist.user.exception.UserValidationException;
import com.my.sorted_playlist.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final ImageService imageService;

	@Transactional
	public void register(RegisterRequest registerRequest, MultipartFile profileImage){
		checkEmailValidation(registerRequest.email());
		String imageUrl = imageService.saveImage(profileImage);

		userRepository.save(registerRequest.toEntity(
			registerRequest.email(),
			passwordEncoder.encode(registerRequest.password()),
			registerRequest.nickname(),
			imageUrl,
			Role.USER
		));
		log.info("success to register");
	}

	@Transactional(readOnly = true)
	public UserResponse logIn(LogInRequest logInRequest) {
		User user = checkEmailAndPassword(logInRequest);
		log.info("success to login");
		return new UserResponse(user.getId(),user.getEmail(),user.getNickname(),user.getProfileImage()); // 비밀번호를 제거한 user 객체 반환
	}

	@Transactional
	public User editUserInfo(User currUser, String nickname, MultipartFile profileImage){
		if(nickname != null && !nickname.isBlank())
			currUser.editNickname(nickname);
		if(profileImage != null && !profileImage.isEmpty()){
			String imageUrl = imageService.saveImage(profileImage);
			imageService.deleteImage(currUser.getProfileImage());
			currUser.editProfileImage(imageUrl);
		}
		userRepository.save(currUser);
		log.info("success to edit user information");
		return currUser;
	}

	@Transactional(readOnly = true)
	public UserInfoResponse getUserInfo(User user){
		return new UserInfoResponse(user.getEmail(), user.getNickname(), user.getProfileImage());
	}

	@Transactional
	public void unregister(User user, String password){
		checkPassword(user, password);
		imageService.deleteImage(user.getProfileImage());
		user.deleteUser();
		log.info("success to unregister");
	}

	private User checkEmailAndPassword(LogInRequest logInRequest) {
		User user = checkEmail(logInRequest);
		checkPassword(user, logInRequest.password());
		return user;
	}
	private void checkPassword(User user, String password) {
		String encodedPassword = user.getPassword();
		if (! passwordEncoder.matches(password, encodedPassword))
			throw new UserPermissionException(HttpStatus.UNAUTHORIZED.value(), "비밀번호가 틀렸습니다.");
	}

	@Transactional(readOnly = true)
	public User checkEmail(LogInRequest logInRequest) {
		return userRepository.findByEmail(logInRequest.email())
			.orElseThrow(() -> new UserPermissionException(HttpStatus.UNAUTHORIZED.value(), "가입되지 않은 이메일 입니다."));
	}

	@Transactional(readOnly = true)
	public void checkEmailValidation(String email) {
		if(userRepository.existsByEmail(email))
			throw new UserValidationException("이미 사용 중인 이메일 입니다.");
	}
}
