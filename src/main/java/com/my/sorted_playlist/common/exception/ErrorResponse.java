package com.my.sorted_playlist.common.exception;

import java.util.ArrayList;


public record ErrorResponse(int status,
							String error,
							ArrayList<String> messages) {
}
