package com.my.sorted_playlist.playlist.exception;

import lombok.Getter;

@Getter
public class PlaylistPermissionException extends RuntimeException{
	private final int status;
	private final String error;

	public PlaylistPermissionException(int status, String error){
		this.status = status;
		this.error = error;
	}
}
