package com.my.sorted_playlist.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.my.sorted_playlist.user.exception.UserPermissionException;
import com.my.sorted_playlist.user.exception.UserValidationException;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(RequestException.class)
	protected ResponseEntity<Object> handler(RequestException e){
		return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getError(), e.getMessages()));
	}

	@ExceptionHandler(UserValidationException.class)
	protected ResponseEntity<Object> handler(UserValidationException e){
		return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getError(), null));
	}

	@ExceptionHandler(UserPermissionException.class)
	protected ResponseEntity<Object> handler(UserPermissionException e){
		return ResponseEntity.status(e.getStatus()).body(new ErrorResponse(e.getStatus(), e.getError(), null));
	}

	@ExceptionHandler(UserAccessException.class)
	protected ResponseEntity<Object> handler(UserAccessException e){
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), e.getError(), null));
	}
}
