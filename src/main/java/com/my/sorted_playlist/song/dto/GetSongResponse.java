package com.my.sorted_playlist.song.dto;

import java.time.LocalDateTime;

import com.my.sorted_playlist.song.domain.Song;

public record GetSongResponse(Long id,
							  String title,
							  String singer,
							  String songLength,
							  Integer songPlayCount,
							  String songVideoId,
							  LocalDateTime addedDateTime,
							  LocalDateTime lastPlayedDateTime) {
	public static GetSongResponse toDTO(Song song){
		return new GetSongResponse(
			song.getId(),
			song.getTitle(),
			song.getSinger(),
			song.getSongLength(),
			song.getSongPlayCount(),
			song.getSongVideoId(),
			song.getAddedDateTime(),
			song.getLastPlayedDateTime()
		);
	}
}
