package com.my.sorted_playlist.song.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.my.sorted_playlist.common.annotation.CurrentUser;
import com.my.sorted_playlist.common.exception.RequestException;
import com.my.sorted_playlist.song.dto.EditSongRequest;
import com.my.sorted_playlist.song.enumerate.Order;
import com.my.sorted_playlist.song.dto.AddSongRequest;
import com.my.sorted_playlist.song.dto.GetSongResponse;
import com.my.sorted_playlist.song.service.SongService;
import com.my.sorted_playlist.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/song")
@Tag(name = "노래 컨트롤러", description = "플레이리스트 내 노래들에 관련된 API 명세서")
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

	@GetMapping
	@Operation(summary = "플레이리스트의 노래 목록 조회 API", description = "선택한 플레이리스트의 노래 목록을 조회하는 API")
	public ResponseEntity<List<GetSongResponse>> getSongs(@CurrentUser User user, @RequestParam Long playlistId){
		List<GetSongResponse> response = songService.getSongsInPlaylist(user, playlistId);
		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping
	@Operation(summary = "플레이리스트에서 노래 삭제 API", description = "플레이리스트에서 선택한 노래를 삭제하는 API")
	public ResponseEntity<Object> deleteSong(@CurrentUser User user, @RequestParam Long songId){
		songService.deleteSongFromPlaylist(user, songId);
		return ResponseEntity.ok().body("OK");
	}

	@GetMapping("/{order}")
	@Operation(summary = "선택한 기준으로 정렬된 노래 목록 조회 API", description = "플레이리스트를 선택한 기준으로 정렬해서 노래 목록을 조회하는 API")
	public ResponseEntity<List<GetSongResponse>> getSongsOrderBy(@CurrentUser User user, @RequestParam Long playlistId, @PathVariable("order") Order order){
		List<GetSongResponse> response = songService.getSongsOrderBy(user, playlistId, order);
		return ResponseEntity.ok().body(response);
	}

	@PatchMapping
	@Operation(summary = "선택한 노래의 제목, 가수 이름 수정 API", description = "선택한 노래의 제목, 가수 이름을 수정하는 API")
	public ResponseEntity<Object> editSong(@CurrentUser User user, @Valid @RequestBody EditSongRequest request, Errors errors){
		if (errors.hasErrors())
			throw new RequestException("노래 제목, 가수 수정의 입력이 올바르지 않습니다.",errors);
		songService.editSong(user, request);
		return ResponseEntity.ok().body("OK");
	}

	@PostMapping("/play")
	@Operation(summary = "최근 재생한 날짜/시간, 재생 횟수 업데이트 API", description = "노래 재생 시 최근 재생한 날짜/시간, 재생 횟수를 업데이트하는 API")
	public ResponseEntity<Object> updateSongPlayInfo(@CurrentUser User user, @RequestParam Long songId){
		songService.updateSongPlayInfo(user,songId);
		return ResponseEntity.ok().body("OK");
	}
}
