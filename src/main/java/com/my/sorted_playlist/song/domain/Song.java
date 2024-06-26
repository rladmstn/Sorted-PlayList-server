package com.my.sorted_playlist.song.domain;

import java.time.LocalDateTime;

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
	private String songLength;
	private Integer songPlayCount;
	private String songVideoId;
	private LocalDateTime addedDateTime;
	private LocalDateTime lastPlayedDateTime;

	public void editTitle(String title){
		this.title = title;
	}
	public void editSinger(String singer){
		this.singer = singer;
	}
	public void updatePlayInfo(){
		this.songPlayCount += 1;
		this.lastPlayedDateTime = LocalDateTime.now();
	}

	@Builder
	public Song(Playlist playlist, String title, String singer, String songLength, Integer songPlayCount,
		String songVideoId,
		LocalDateTime addedDateTime, LocalDateTime lastPlayedDateTime) {
		this.playlist = playlist;
		this.title = title;
		this.singer = singer;
		this.songLength = songLength;
		this.songPlayCount = songPlayCount;
		this.songVideoId = songVideoId;
		this.addedDateTime = addedDateTime;
		this.lastPlayedDateTime = lastPlayedDateTime;
	}
}
