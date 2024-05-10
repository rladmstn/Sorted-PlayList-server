package com.my.sorted_playlist.song.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.my.sorted_playlist.playlist.domain.Playlist;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Song {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "playlist_id")
	private Playlist playlist;

	private String title;
	private String singer;
	private LocalTime songLength;
	private Integer songPlayCount;
	private String songUrl;
	private LocalDateTime addedDateTime;
	private LocalDateTime lastPlayedDateTime;

	@Builder
	public Song(Playlist playlist, String title, String singer, LocalTime songLength, Integer songPlayCount,
		String songUrl,
		LocalDateTime addedDateTime, LocalDateTime lastPlayedDateTime) {
		this.playlist = playlist;
		this.title = title;
		this.singer = singer;
		this.songLength = songLength;
		this.songPlayCount = songPlayCount;
		this.songUrl = songUrl;
		this.addedDateTime = addedDateTime;
		this.lastPlayedDateTime = lastPlayedDateTime;
	}
}
