package com.my.sorted_playlist.song.exception;

import lombok.Getter;

@Getter
public class SongPermissionException extends RuntimeException{
	private final int status;
	private final String error;

	public SongPermissionException(int status, String error){
		this.status = status;
		this.error = error;
	}
}
