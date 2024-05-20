package com.my.sorted_playlist.song.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.my.sorted_playlist.common.annotation.CurrentUser;
import com.my.sorted_playlist.common.exception.RequestException;
import com.my.sorted_playlist.song.dto.AddSongRequest;
import com.my.sorted_playlist.song.service.SongService;
import com.my.sorted_playlist.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/song")
@Tag(name = "노래 관련 컨트롤러", description = "플레이리스트 내 노래들에 관련된 API 명세서")
public class SongController {
	private final SongService songService;

	@PostMapping
	@Operation(summary = "노래 추가하는 API", description = "노래를 선택한 플레이리스트에 추가하는 API")
	public ResponseEntity<Object> addSong(@CurrentUser User user, @Valid @RequestBody AddSongRequest request, Errors errors){
		if(errors.hasErrors())
			throw new RequestException("노래 추가 입력이 올바르지 않습니다.",errors);
		songService.addSongToPlaylist(user,request);
		return ResponseEntity.ok().body("OK");
	}
}
