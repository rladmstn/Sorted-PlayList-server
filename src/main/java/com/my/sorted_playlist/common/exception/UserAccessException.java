package com.my.sorted_playlist.common.exception;

import lombok.Getter;

@Getter
public class UserAccessException extends RuntimeException{
	private final String error;

	public UserAccessException(String error) {
		this.error = error;
	}
}
