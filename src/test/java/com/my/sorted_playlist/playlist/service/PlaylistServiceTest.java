package com.my.sorted_playlist.playlist.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.my.sorted_playlist.common.enums.Role;
import com.my.sorted_playlist.playlist.domain.Playlist;
import com.my.sorted_playlist.playlist.repository.PlaylistRepository;
import com.my.sorted_playlist.user.domain.User;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceTest {
	@InjectMocks
	private PlaylistService playListService;

	@Mock
	private PlaylistRepository playListRepository;

	@Captor
	private ArgumentCaptor<Playlist> playListCaptor;

	private final String playListName = "name";
	private User user;
	@BeforeEach
	void set(){
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();
	}

	@Test
	@DisplayName("플레이리스트 생성 성공")
	void 플레이리스트생성_성공(){
		// given
		// when
		playListService.createPlayList(user,playListName);
		// then
		verify(playListRepository,times(1)).save(playListCaptor.capture());
		Playlist playList = playListCaptor.getValue();
		assertThat(playList.getName()).isEqualTo(playListName);
		assertThat(playList.getUser()).isEqualTo(user);
		assertThat(playList.getSongCount()).isEqualTo(0);
	}


}