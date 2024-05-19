package com.my.sorted_playlist.playlist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EditPlaylistNameRequest(
	@NotNull(message = "수정할 플레이리스트 id는 필수 입력입니다.") Long playlistId,
	@NotBlank(message = "새로운 플레이리스트 이름은 필수 입력입니다.") String name) {
}
