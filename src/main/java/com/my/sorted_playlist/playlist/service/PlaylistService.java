package com.my.sorted_playlist.playlist.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.sorted_playlist.playlist.domain.Playlist;
import com.my.sorted_playlist.playlist.dto.EditPlaylistNameRequest;
import com.my.sorted_playlist.playlist.dto.GetPlaylistResponse;
import com.my.sorted_playlist.playlist.exception.PlaylistPermissionException;
import com.my.sorted_playlist.playlist.exception.PlaylistRequestException;
import com.my.sorted_playlist.playlist.repository.PlaylistRepository;
import com.my.sorted_playlist.user.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PlaylistService {
	private final PlaylistRepository playlistRepository;

	public void createPlayList(User user, String name){
		if(playlistRepository.existsByUserAndName(user,name))
			throw new PlaylistRequestException("중복되는 이름의 플레이리스트가 있습니다.");
		playlistRepository.save(Playlist.builder()
			.user(user)
			.name(name)
			.createdDateTime(LocalDateTime.now())
			.songCount(0)
			.build());
		log.info("success to create playlist");
	}

	public Playlist editPlayListName(User user, EditPlaylistNameRequest request) {
		Playlist playlist = playlistRepository.findById(request.playlistId())
			.orElseThrow(() -> new PlaylistPermissionException(HttpStatus.UNAUTHORIZED.value(), "존재하지 않는 플레이리스트 입니다."));

		if(!playlist.getUser().getId().equals(user.getId()))
			throw new PlaylistPermissionException(HttpStatus.FORBIDDEN.value(), "플레이리스트의 주인과 사용자가 일치하지 않습니다.");

		playlist.editName(request.name());
		log.info("success to edit playlist name");
		return playlist;
	}

	public List<GetPlaylistResponse> getPlayLists(User user) {
		List<GetPlaylistResponse> response = playlistRepository.findAllByUser(user)
			.stream().map(Playlist::toDTO).toList();
		log.info("success to get playlists");
		return response;
	}
}
