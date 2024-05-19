package com.my.sorted_playlist.playlist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.sorted_playlist.playlist.domain.Playlist;
import com.my.sorted_playlist.user.domain.User;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {
	List<Playlist> findAllByUser(User user);
}
