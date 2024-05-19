package com.my.sorted_playlist.playlist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.sorted_playlist.common.annotation.CurrentUser;
import com.my.sorted_playlist.common.exception.RequestException;
import com.my.sorted_playlist.playlist.dto.EditPlaylistNameRequest;
import com.my.sorted_playlist.playlist.service.PlaylistService;
import com.my.sorted_playlist.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/playlist")
@Tag(name = "플레이리스트 컨트롤러", description = "플레이리스트 관련된 API 명세서")
public class PlaylistController {
	private final PlaylistService playListService;

	@PostMapping
	@Operation(summary = "플레이리스트 생성", description = "플레이리스트 이름을 넣어 새로운 플레이리스트를 생성하는 API")
	public ResponseEntity<Object> createPlayList(@CurrentUser User user, @RequestParam String name){
		playListService.createPlayList(user,name);
		return ResponseEntity.ok().body("OK");
	}

	@PatchMapping
	@Operation(summary = "플레이리스트 이름 수정", description = "플레이리스트 이름을 변경하는 API")
	public ResponseEntity<Object> editPlayListName(@CurrentUser User user, @Valid @RequestBody EditPlaylistNameRequest editPlaylistNameRequest, Errors errors){
		if(errors.hasErrors())
			throw new RequestException("플레이리스트 수정의 입력이 올바르지 않습니다.",errors);
		playListService.editPlayListName(user,editPlaylistNameRequest);
		return ResponseEntity.ok().body("OK");
	}
}
