package com.my.sorted_playlist.song.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
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
import com.my.sorted_playlist.song.dto.GetSongResponse;
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
	private Song song1;
	private Song song2;
	private final String songVideoId = "SONGVIDEOID";
	private final String title = "title";
	private final String singer = "singer";
	private final String songLength = "3:03";
	private final Long playlistId = 10L;
	private final Long songId1 = 30L;
	private final Long songId2 = 31L;

	@BeforeEach
	void set() throws IllegalAccessException, NoSuchFieldException {
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();
		diffUser = User.builder().email("email").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();

		playlist = Playlist.builder().name("playlist").user(user).createdDateTime(LocalDateTime.now()).songCount(0).build();

		song1 = Song.builder().playlist(playlist).title("title1").singer("singer1")
			.songLength("songLength1").songPlayCount(0).songVideoId("songVideoId1")
			.addedDateTime(LocalDateTime.now()).lastPlayedDateTime(LocalDateTime.now()).build();
		song2 = Song.builder().playlist(playlist).title("title2").singer("singer2")
			.songLength("songLength2").songPlayCount(0).songVideoId("songVideoId2")
			.addedDateTime(LocalDateTime.now()).lastPlayedDateTime(LocalDateTime.now()).build();

		Field userIdField = User.class.getDeclaredField("id");
		userIdField.setAccessible(true);
		userIdField.set(user, 1L);
		userIdField.set(diffUser,2L);

		Field playlistIdField = Playlist.class.getDeclaredField("id");
		playlistIdField.setAccessible(true);
		playlistIdField.set(playlist, playlistId);

		Field songIdField = Song.class.getDeclaredField("id");
		songIdField.setAccessible(true);
		songIdField.set(song1,songId1);
		songIdField.set(song2,songId2);
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

	@Test
	@DisplayName("플레이리스트의 노래 목록 조회 성공")
	void 플레이리스트노래목록조회_성공(){
		// given
		List<Song> songs = Arrays.asList(song1, song2);
		when(playlistRepository.findById(playlistId)).thenReturn(Optional.ofNullable(playlist));
		when(songRepository.findAllByPlaylist(playlist)).thenReturn(songs);
		// when
		List<GetSongResponse> result = songService.getSongsInPlaylist(user, playlistId);
		// then
		verify(songRepository,times(1)).findAllByPlaylist(playlist);
		assertThat(result).hasSize(2);
		assertThat(result.get(0).title()).isEqualTo("title1");
		assertThat(result.get(1).title()).isEqualTo("title2");
		assertThat(result.get(0).id()).isEqualTo(songId1);
		assertThat(result.get(1).id()).isEqualTo(songId2);
	}

}