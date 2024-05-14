package com.my.sorted_playlist.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.my.sorted_playlist.user.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {
	boolean existsByEmail(String email);
}
