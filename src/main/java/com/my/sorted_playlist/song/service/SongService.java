package com.my.sorted_playlist.song.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.sorted_playlist.playlist.domain.Playlist;
import com.my.sorted_playlist.playlist.exception.PlaylistPermissionException;
import com.my.sorted_playlist.playlist.repository.PlaylistRepository;
import com.my.sorted_playlist.song.dto.AddSongRequest;
import com.my.sorted_playlist.song.dto.GetSongResponse;
import com.my.sorted_playlist.song.repository.SongRepository;
import com.my.sorted_playlist.user.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SongService {
	private final SongRepository songRepository;
	private final PlaylistRepository playlistRepository;
	public void addSongToPlaylist(User user, AddSongRequest addSongRequest) {
		Playlist playlist = checkPermission(addSongRequest.playlistId(), user);
		songRepository.save(addSongRequest.toEntity(playlist));
		playlist.updateSongCount(+1);
		log.info("success to add song to playlist");
	}

	public List<GetSongResponse> getSongsInPlaylist(User user, Long playlistId) {
		Playlist playlist = checkPermission(playlistId, user);
		List<GetSongResponse> result = songRepository.findAllByPlaylist(playlist)
			.stream().map(GetSongResponse::toDTO).toList();
		log.info("success to get songs in playlist");
		return result;
	}

	private Playlist checkPermission(Long playlistId, User user) {
		Playlist playlist = playlistRepository.findById(playlistId)
			.orElseThrow(() -> new PlaylistPermissionException(HttpStatus.UNAUTHORIZED.value(), "존재하지 않는 플레이리스트 입니다."));

		if (!playlist.getUser().getId().equals(user.getId()))
			throw new PlaylistPermissionException(HttpStatus.FORBIDDEN.value(), "플레이리스트의 주인과 사용자가 일치하지 않습니다.");
		return playlist;
	}
}
