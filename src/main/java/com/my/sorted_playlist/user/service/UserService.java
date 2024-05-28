package com.my.sorted_playlist.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

	public void register(RegisterRequest registerRequest, MultipartFile profileImage){
		validateDuplicatedEmail(registerRequest.email());
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

	public void validateDuplicatedEmail(String email){
		if(userRepository.existsByEmail(email))
			throw new UserValidationException("이미 가입된 이메일 입니다.");
	}

	public UserResponse logIn(LogInRequest logInRequest) {
		User user = checkEmailAndPassword(logInRequest);
		log.info("success to login");
		return new UserResponse(user.getId(),user.getEmail(),user.getNickname(),user.getProfileImage()); // 비밀번호를 제거한 user 객체 반환
	}

	public User editUserInfo(User currUser, String nickname, MultipartFile profileImage){
		User user = userRepository.findByIdAndEmail(currUser.getId(), currUser.getEmail());
		if(nickname != null && !nickname.isBlank())
			user.editNickname(nickname);
		if(profileImage != null && !profileImage.isEmpty()){
			String imageUrl = imageService.saveImage(profileImage);
			imageService.deleteImage(user.getProfileImage());
			user.editProfileImage(imageUrl);
		}
		log.info("success to edit user information");
		return user;
	}

	public UserInfoResponse getUserInfo(User user){
		return new UserInfoResponse(user.getEmail(), user.getNickname(), user.getProfileImage());
	}

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
	private User checkEmail(LogInRequest logInRequest) {
		return userRepository.findByEmail(logInRequest.email())
			.orElseThrow(() -> new UserPermissionException(HttpStatus.UNAUTHORIZED.value(), "가입되지 않은 이메일 입니다."));
	}

}
