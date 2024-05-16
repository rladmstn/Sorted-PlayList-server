package com.my.sorted_playlist.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LogInRequest(@NotBlank(message = "이메일은 필수 입력 값입니다.") String email,
						   @NotBlank(message = "비밀 번호는 필수 입력 값입니다.") String password) {
}
