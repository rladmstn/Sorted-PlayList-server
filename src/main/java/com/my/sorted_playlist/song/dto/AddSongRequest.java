package com.my.sorted_playlist.song.dto;

import java.time.LocalDateTime;

import com.my.sorted_playlist.playlist.domain.Playlist;
import com.my.sorted_playlist.song.domain.Song;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddSongRequest(@NotNull(message = "플레이리스트 아이디는 필수 입력입니다.") Long playlistId,
							 @NotBlank(message = "video id는 필수 입력입니다.") String songVideoId,
							 @NotBlank(message = "노래 제목은 필수 입력입니다.") String title,
							 @NotBlank(message = "노래 가수는 필수 입력입니다.") String singer,
							 @NotBlank(message = "노래 길이는 필수 입력입니다.") String songLength
							 ) {
	public Song toEntity(Playlist playlist){
		return Song.builder()
			.playlist(playlist)
			.songVideoId(songVideoId)
			.title(title)
			.singer(singer)
			.songLength(songLength)
			.songPlayCount(0)
			.addedDateTime(LocalDateTime.now())
			.lastPlayedDateTime(LocalDateTime.now())
			.build();
	}
}
