package com.my.sorted_playlist.song.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.my.sorted_playlist.common.enums.Role;
import com.my.sorted_playlist.playlist.domain.Playlist;
import com.my.sorted_playlist.playlist.exception.PlaylistPermissionException;
import com.my.sorted_playlist.playlist.repository.PlaylistRepository;
import com.my.sorted_playlist.song.domain.Song;
import com.my.sorted_playlist.song.dto.AddSongRequest;
import com.my.sorted_playlist.song.repository.SongRepository;
import com.my.sorted_playlist.user.domain.User;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {
	@InjectMocks
	private SongService songService;
	@Mock
	private SongRepository songRepository;
	@Mock
	private PlaylistRepository playlistRepository;
	@Captor
	private ArgumentCaptor<Song> captor;

	private User user;
	private User diffUser;
	private Playlist playlist;
	private final String songVideoId = "SONGVIDEOID";
	private final String title = "title";
	private final String singer = "singer";
	private final String songLength = "3:03";

	@BeforeEach
	void set() throws IllegalAccessException, NoSuchFieldException {
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();
		diffUser = User.builder().email("email").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();

		playlist = Playlist.builder().name("playlist").user(user).createdDateTime(LocalDateTime.now()).songCount(0).build();

		Field userIdField = User.class.getDeclaredField("id");
		userIdField.setAccessible(true);
		userIdField.set(user, 1L);

		Field userIdField2 = User.class.getDeclaredField("id");
		userIdField2.setAccessible(true);
		userIdField2.set(diffUser,2L);

		Field playlistIdField = Playlist.class.getDeclaredField("id");
		playlistIdField.setAccessible(true);
		playlistIdField.set(playlist, 10L);
	}

	@Test
	@DisplayName("플레이리스트에 노래 추가 성공")
	void 플레이리스트에노래추가_성공() {
		// given
		AddSongRequest request = new AddSongRequest(10L,
			songVideoId,title,singer,songLength);
		when(playlistRepository.findById(10L)).thenReturn(Optional.ofNullable(playlist));
		// when
		songService.addSongToPlaylist(user,request);
		// then
		verify(songRepository,times(1)).save(captor.capture());
		Song song = captor.getValue();
		assertThat(song.getPlaylist()).isEqualTo(playlist);
		assertThat(song.getTitle()).isEqualTo(title);
		assertThat(song.getSinger()).isEqualTo(singer);
		assertThat(song.getSongLength()).isEqualTo(songLength);
	}
	@Test
	@DisplayName("플레이리스트에 노래 추가 실패 : 존재하지 않는 플레이리스트")
	void 플레이리스트에노래추가_실패_존재하지않는플레이리스트() {
		// given
		AddSongRequest request = new AddSongRequest(11L, songVideoId,title,singer,songLength);
		when(playlistRepository.findById(11L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> songService.addSongToPlaylist(user,request))
			.isInstanceOf(PlaylistPermissionException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 플레이리스트 입니다.");
	}

	@Test
	@DisplayName("플레이리스트에 노래 추가 실패 : 주인 불일치")
	void 플레이리스트에노래추가_실패_주인불일치() {
		// given
		AddSongRequest request = new AddSongRequest(10L, songVideoId,title,singer,songLength);
		when(playlistRepository.findById(10L)).thenReturn(Optional.ofNullable(playlist));

		// when, then
		assertThatThrownBy(() -> songService.addSongToPlaylist(diffUser,request))
			.isInstanceOf(PlaylistPermissionException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","플레이리스트의 주인과 사용자가 일치하지 않습니다.");
	}
}