package com.my.sorted_playlist.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import com.my.sorted_playlist.common.enums.Role;
import com.my.sorted_playlist.user.domain.User;
import com.my.sorted_playlist.user.dto.LogInRequest;
import com.my.sorted_playlist.user.dto.RegisterRequest;
import com.my.sorted_playlist.user.dto.UserResponse;
import com.my.sorted_playlist.user.exception.LogInException;
import com.my.sorted_playlist.user.exception.UserValidationException;
import com.my.sorted_playlist.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
	@InjectMocks
	private UserService userService;
	@Mock
	private ImageService imageService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Captor
	private ArgumentCaptor<User> userCaptor;

	private final String email = "test@email.com";
	private final String password = "password";
	private final String nickname = "nickname";
	private final String encoded = "encoded";
	private final String imageUrl = "imageUrl";
	private final String newImageUrl = "newImageUrl";

	private RegisterRequest registerRequest;
	private LogInRequest logInRequest;

	@BeforeEach
	void setup(){
		registerRequest = new RegisterRequest(email, password, nickname);
		logInRequest = new LogInRequest(email, password);
	}

	@Test
	@DisplayName("회원가입 성공")
	void 회원가입_성공(){
		// given
		MockMultipartFile profileImage = new MockMultipartFile("profileImage",new byte[]{1,2,3});

		when(passwordEncoder.encode(registerRequest.password())).thenReturn(encoded);
		when(imageService.saveImage(profileImage)).thenReturn(imageUrl);
		// when
		userService.register(registerRequest,profileImage);
		// then
		verify(userRepository, times(1)).save(userCaptor.capture());
		User saved = userCaptor.getValue();
		assertThat(saved.getEmail()).isEqualTo(email);
		assertThat(saved.getPassword()).isEqualTo(encoded);
		assertThat(saved.getNickname()).isEqualTo(nickname);
		assertThat(saved.getProfileImage()).isEqualTo(imageUrl);
	}

	@Test
	@DisplayName("회원가입 실패 : 이메일 중복")
	void 회원가입_실패_이메일중복(){
		// given
		MockMultipartFile profileImage = new MockMultipartFile("profileImage",new byte[]{1,2,3});
		when(userRepository.existsByEmail(email)).thenReturn(true);
		// when, then
		assertThatThrownBy(()->userService.register(registerRequest,profileImage))
			.isInstanceOf(UserValidationException.class);
	}

	@Test
	@DisplayName("로그인 성공")
	void 로그인_성공() {
		// given
		User user = new User(email,password,nickname,imageUrl,Role.USER);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(logInRequest.password(),user.getPassword())).thenReturn(true);
		// when
		UserResponse userResponse = userService.logIn(logInRequest);
		// then
		assertThat(userResponse.email()).isEqualTo(email);
		assertThat(userResponse.nickname()).isEqualTo(nickname);
		assertThat(userResponse.profileImage()).isEqualTo(imageUrl);
	}

	@Test
	@DisplayName("로그인 실패 : 가입되지 않은 이메일")
	void 로그인_실패_가입되지않은이메일(){
		// given
		// when, then
		assertThatThrownBy(() -> userService.logIn(logInRequest))
			.isInstanceOf(LogInException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.UNAUTHORIZED.value())
			.hasFieldOrPropertyWithValue("error","가입되지 않은 이메일 입니다.");
	}

	@Test
	@DisplayName("로그인 실패 : 틀린 비밀번호")
	void 로그인_실패_틀린비밀번호(){
		// given
		User user = new User(email,password,nickname,imageUrl,Role.USER);
		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		// when, then
		assertThatThrownBy(() -> userService.logIn(logInRequest))
			.isInstanceOf(LogInException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.UNAUTHORIZED.value())
			.hasFieldOrPropertyWithValue("error","비밀번호가 틀렸습니다.");
	}

	@Test
	@DisplayName("회원 정보 수정 성공")
	void 회원정보수정_성공(){
		// given
		MockMultipartFile newImage = new MockMultipartFile("newImage",new byte[]{3,3,3});
		when(imageService.saveImage(any(MultipartFile.class))).thenReturn(newImageUrl);

		User user = User.builder().email(email).password(password).nickname(nickname).profileImage(imageUrl).role(Role.USER).build();
		String newNickname = "newNickname";
		// when
		User edited = userService.editUserInfo(user, newNickname, newImage);
		// then
		verify(imageService,times(1)).saveImage(newImage);
		verify(imageService,times(1)).deleteImage(imageUrl);
		assertThat(edited.getProfileImage()).isEqualTo(newImageUrl);
		assertThat(edited.getNickname()).isEqualTo(newNickname);
	}
}