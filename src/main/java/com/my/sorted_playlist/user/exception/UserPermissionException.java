package com.my.sorted_playlist.user.exception;

import lombok.Getter;

@Getter
public class UserPermissionException extends RuntimeException{
	private final int status;
	private final String error;

	public UserPermissionException(int status, String error) {
		this.status = status;
		this.error = error;
	}
}
