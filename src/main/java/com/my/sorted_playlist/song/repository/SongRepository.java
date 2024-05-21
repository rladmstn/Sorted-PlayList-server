package com.my.sorted_playlist.song.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.sorted_playlist.playlist.domain.Playlist;
import com.my.sorted_playlist.song.domain.Song;

public interface SongRepository extends JpaRepository<Song,Long> {
	List<Song> findAllByPlaylist(Playlist playlist);
}
