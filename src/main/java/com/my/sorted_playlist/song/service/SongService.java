package com.my.sorted_playlist.song.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.my.sorted_playlist.playlist.domain.Playlist;
import com.my.sorted_playlist.playlist.exception.PlaylistPermissionException;
import com.my.sorted_playlist.playlist.repository.PlaylistRepository;
import com.my.sorted_playlist.song.enumerate.Order;
import com.my.sorted_playlist.song.domain.Song;
import com.my.sorted_playlist.song.dto.AddSongRequest;
import com.my.sorted_playlist.song.dto.GetSongResponse;
import com.my.sorted_playlist.song.exception.SongPermissionException;
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
		Playlist playlist = checkPlaylistPermission(addSongRequest.playlistId(), user);
		songRepository.save(addSongRequest.toEntity(playlist));
		playlist.updateSongCount(+1);
		log.info("success to add song to playlist");
	}

	public List<GetSongResponse> getSongsInPlaylist(User user, Long playlistId) {
		Playlist playlist = checkPlaylistPermission(playlistId, user);
		List<GetSongResponse> result = songRepository.findAllByPlaylist(playlist)
			.stream().map(GetSongResponse::toDTO).toList();
		log.info("success to get songs in playlist");
		return result;
	}

	public List<GetSongResponse> getSongsOrderBy(User user, Long playlistId, Order order) {
		Playlist playlist = checkPlaylistPermission(playlistId, user);
		List<Song> songs = getOrderedSongs(order, playlist);
		List<GetSongResponse> result = songs.stream().map(GetSongResponse::toDTO).toList();
		log.info("success to get ordered songs in playlist ");
		return result;
	}

	private List<Song> getOrderedSongs(Order order, Playlist playlist) {
		return switch (order) {
			case TITLE -> songRepository.findAllByPlaylistOrderByTitle(playlist);
			case ADDED_TIME -> songRepository.findAllByPlaylistOrderByAddedDateTimeDesc(playlist);
			case PLAY_COUNT -> songRepository.findAllByPlaylistOrderBySongPlayCountDesc(playlist);
			case LAST_PLAYED_TIME -> songRepository.findAllByPlaylistOrderByLastPlayedDateTimeDesc(playlist);
		};
	}

	public void deleteSongFromPlaylist(User user, Long songId) {
		Song song = checkSongPermission(user, songId);
		Playlist playlist = song.getPlaylist();
		songRepository.delete(song); // 노래 삭제
		playlist.updateSongCount(-1); // 플레이리스트에서 노래 개수 업데이트
		log.info("success to delete song from the playlist");
	}

	private Song checkSongPermission(User user, Long songId) {
		Song song = songRepository.findById(songId)
			.orElseThrow(() -> new SongPermissionException(HttpStatus.UNAUTHORIZED.value(), "플레이리스트에 존재하지 않는 노래입니다."));

		if (!song.getPlaylist().getUser().getId().equals(user.getId()))
			throw new SongPermissionException(HttpStatus.FORBIDDEN.value(), "노래 주인과 사용자가 일치하지 않습니다.");
		return song;
	}

	private Playlist checkPlaylistPermission(Long playlistId, User user) {
		Playlist playlist = playlistRepository.findById(playlistId)
			.orElseThrow(() -> new PlaylistPermissionException(HttpStatus.UNAUTHORIZED.value(), "존재하지 않는 플레이리스트 입니다."));

		if (!playlist.getUser().getId().equals(user.getId()))
			throw new PlaylistPermissionException(HttpStatus.FORBIDDEN.value(), "플레이리스트의 주인과 사용자가 일치하지 않습니다.");
		return playlist;
	}
}
