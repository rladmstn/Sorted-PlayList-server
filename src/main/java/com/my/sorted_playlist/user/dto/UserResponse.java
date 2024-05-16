package com.my.sorted_playlist.user.dto;

public record UserResponse(Long id,
						   String email,
						   String nickname,
						   String profileImage) {
}
