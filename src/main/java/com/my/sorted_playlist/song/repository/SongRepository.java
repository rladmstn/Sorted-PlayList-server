package com.my.sorted_playlist.song.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.sorted_playlist.song.domain.Song;

public interface SongRepository extends JpaRepository<Song,Long> {
}
