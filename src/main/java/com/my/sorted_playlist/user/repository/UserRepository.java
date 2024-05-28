package com.my.sorted_playlist.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.my.sorted_playlist.user.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {

	@Query("select case when count(u)>0 then true else false end from User u where u.deletedDate is null")
	boolean existsByEmail(String email);
	@Query("select u from User u where u.email = :email and u.deletedDate is null ")
	Optional<User> findByEmail(String email);
	@Query("select u from User u where u.id = :id and u.email = :email and u.deletedDate is null")
	User findByIdAndEmail(Long id, String email);
}
