package com.my.sorted_playlist.playlist.dto;

import java.time.LocalDateTime;

public record GetPlaylistResponse(
	Long id,
	String name,
	LocalDateTime createdDateTime,
	Integer songCount
) {
}
