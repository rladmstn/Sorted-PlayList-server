package com.my.sorted_playlist.user.exception;

import lombok.Getter;

@Getter
public class LogInException extends RuntimeException{
	private final int status;
	private final String error;

	public LogInException(int status, String error) {
		this.status = status;
		this.error = error;
	}
}
