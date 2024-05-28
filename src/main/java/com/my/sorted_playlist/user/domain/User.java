package com.my.sorted_playlist.user.domain;

import java.time.LocalDate;
import com.my.sorted_playlist.common.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
	private Role role;

	private LocalDate deletedDate = null;
	@Builder
	public User(String email, String password, String nickname, String profileImage, Role role) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.profileImage = profileImage;
		this.role = role;
	}

	public void editNickname(String nickname){
		this.nickname = nickname;
	}
	public void editProfileImage(String profileImage){
		this.profileImage = profileImage;
	}

	public void deleteUser(){
		this.deletedDate = LocalDate.now();
	}
}
