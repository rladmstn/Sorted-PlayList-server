package com.my.sorted_playlist.playlist.exception;

import lombok.Getter;

@Getter
public class PlaylistRequestException extends RuntimeException{
	private final String error;

	public PlaylistRequestException(String error) {
		this.error = error;
	}
}
