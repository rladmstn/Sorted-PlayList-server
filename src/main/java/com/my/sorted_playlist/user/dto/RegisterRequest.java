package com.my.sorted_playlist.user.dto;

import com.my.sorted_playlist.common.enums.Role;
import com.my.sorted_playlist.user.domain.User;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank(message = "이메일은 필수 입력입니다.") String email,
							  @NotBlank(message = "비밀번호는 필수 입력입니다.") String password,
							  @NotBlank(message = "닉네임은 필수 입력입니다.") String nickname) {
	public User toEntity(String email, String password, String nickname, String profileImage, Role role){
		return User.builder()
			.email(email)
			.password(password)
			.nickname(nickname)
			.profileImage(profileImage)
			.role(role)
			.build();
	}
}
