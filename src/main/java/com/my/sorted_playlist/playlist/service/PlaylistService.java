package com.my.sorted_playlist.playlist.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.my.sorted_playlist.playlist.domain.Playlist;
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
	private final PlaylistRepository playListRepository;

	public void createPlayList(User user, String name){
		playListRepository.save(Playlist.builder()
			.user(user)
			.name(name)
			.createdDateTime(LocalDateTime.now())
			.songCount(0)
			.build());
		log.info("success to create playlist");
	}
}
