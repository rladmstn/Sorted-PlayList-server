package com.my.sorted_playlist.song.dto;

import jakarta.validation.constraints.NotNull;

public record EditSongRequest(@NotNull(message = "노래 id는 필수 입력입니다.") Long songId,
							  String title,
							  String singer) {
}
