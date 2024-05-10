package com.my.sorted_playlist.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	private String email;
	private String password; // Encoding 필요
	private String nickname;
	private String profileImage;

	@Builder
	public User(String email, String password, String nickname, String profileImage) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.profileImage = profileImage;
	}
}
