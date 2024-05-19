package com.my.sorted_playlist.playlist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.sorted_playlist.playlist.domain.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {
}
