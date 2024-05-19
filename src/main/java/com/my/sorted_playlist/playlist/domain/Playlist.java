package com.my.sorted_playlist.playlist.domain;

import java.time.LocalDateTime;

import com.my.sorted_playlist.user.domain.User;

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
public class Playlist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private String name;
	private LocalDateTime createdDateTime;
	private Integer songCount;

	@Builder
	public Playlist(User user, String name, LocalDateTime createdDateTime, Integer songCount) {
		this.user = user;
		this.name = name;
		this.createdDateTime = createdDateTime;
		this.songCount = songCount;
	}

	public void editName(String name){
		this.name = name;
	}
}
