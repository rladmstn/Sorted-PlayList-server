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
import com.my.sorted_playlist.song.enumerate.Order;
import com.my.sorted_playlist.song.domain.Song;
import com.my.sorted_playlist.song.dto.AddSongRequest;
import com.my.sorted_playlist.song.dto.GetSongResponse;
import com.my.sorted_playlist.song.exception.SongPermissionException;
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
	private Song song3;
	private final String songVideoId = "SONGVIDEOID";
	private final String title = "title";
	private final String singer = "singer";
	private final String songLength = "3:03";
	private final Long playlistId = 10L;
	private final Long songId1 = 30L;
	private final Long songId2 = 31L;
	private final Long songId3 = 32L;

	@BeforeEach
	void set() throws IllegalAccessException, NoSuchFieldException {
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();
		diffUser = User.builder().email("email").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();

		playlist = Playlist.builder().name("playlist").user(user).createdDateTime(LocalDateTime.now()).songCount(0).build();

		song1 = Song.builder().playlist(playlist).title("ATitle").singer("singer1")
			.songLength("songLength1").songPlayCount(2).songVideoId("songVideoId1")
			.addedDateTime(LocalDateTime.now().plusDays(3)).lastPlayedDateTime(LocalDateTime.now().plusDays(7)).build();
		song2 = Song.builder().playlist(playlist).title("CTitle").singer("singer2")
			.songLength("songLength2").songPlayCount(10).songVideoId("songVideoId2")
			.addedDateTime(LocalDateTime.now().plusDays(4)).lastPlayedDateTime(LocalDateTime.now().plusDays(8)).build();
		song3 = Song.builder().playlist(playlist).title("BTitle").singer("singer3")
			.songLength("songLength3").songPlayCount(5).songVideoId("songVideoId3")
			.addedDateTime(LocalDateTime.now().plusDays(2)).lastPlayedDateTime(LocalDateTime.now().plusDays(6)).build();

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
		songIdField.set(song3,songId3);
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
		assertThat(result.get(0).title()).isEqualTo("ATitle");
		assertThat(result.get(1).title()).isEqualTo("CTitle");
		assertThat(result.get(0).id()).isEqualTo(songId1);
		assertThat(result.get(1).id()).isEqualTo(songId2);
	}

	@Test
	@DisplayName("플레이르스트에서 노래 삭제 성공")
	void 플레이리스트에서노래삭제_성공(){
		// given
		when(songRepository.findById(songId1)).thenReturn(Optional.ofNullable(song1));
		// when
		songService.deleteSongFromPlaylist(user,songId1);
		// then
		verify(songRepository,times(1)).delete(song1);
	}

	@Test
	@DisplayName("플레이리스트에서 노래 삭제 실패 : 존재하지 않는 노래")
	void 플레이리스트에서노래삭제_실패_존재하지않는노래(){
		// given
		when(songRepository.findById(songId1)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> songService.deleteSongFromPlaylist(user, songId1))
			.isInstanceOf(SongPermissionException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.UNAUTHORIZED.value())
			.hasFieldOrPropertyWithValue("error","플레이리스트에 존재하지 않는 노래입니다.");
	}

	@Test
	@DisplayName("플레이리스트에서 노래 삭제 실패 : 노래의 주인 불일치")
	void 플레이리스트에서노래삭제_실패_주인불일치(){
		// given
		when(songRepository.findById(songId1)).thenReturn(Optional.ofNullable(song1));
		// when, then
		assertThatThrownBy(() -> songService.deleteSongFromPlaylist(diffUser,songId1))
			.isInstanceOf(SongPermissionException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","노래 주인과 사용자가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("제목 순으로 정렬된 노래 목록 조회 성공")
	void 제목순으로정렬된노래목록조회_성공(){
		// given
		List<Song> songs = Arrays.asList(song1, song3, song2);
		List<GetSongResponse> expected = Arrays.asList(GetSongResponse.toDTO(song1),GetSongResponse.toDTO(song3),GetSongResponse.toDTO(song2));
		when(playlistRepository.findById(playlistId)).thenReturn(Optional.ofNullable(playlist));
		when(songRepository.findAllByPlaylistOrderByTitle(playlist)).thenReturn(songs);
		// when
		List<GetSongResponse> songsOrderBy = songService.getSongsOrderBy(user, playlistId, Order.TITLE);
		// then
		assertThat(songsOrderBy).isEqualTo(expected);
	}
	@Test
	@DisplayName("노래를 최근 추가한 순서로 정렬된 노래 목록 조회 성공")
	void 최근추가한순서로노래목록조회_성공(){
		// given
		List<Song> songs = Arrays.asList(song3, song1, song2);
		List<GetSongResponse> expected = Arrays.asList(GetSongResponse.toDTO(song3),GetSongResponse.toDTO(song1),GetSongResponse.toDTO(song2));
		when(playlistRepository.findById(playlistId)).thenReturn(Optional.ofNullable(playlist));
		when(songRepository.findAllByPlaylistOrderByTitle(playlist)).thenReturn(songs);
		// when
		List<GetSongResponse> songsOrderBy = songService.getSongsOrderBy(user, playlistId, Order.TITLE);
		// then
		assertThat(songsOrderBy).isEqualTo(expected);
	}
	@Test
	@DisplayName("가장 많이 재생한 순으로 정렬된 노래 목록 조회 성공")
	void 가장많이재생한순으로정렬된노래목록조회_성공(){
		// given
		List<Song> songs = Arrays.asList(song2, song3, song1);
		List<GetSongResponse> expected = Arrays.asList(GetSongResponse.toDTO(song2),GetSongResponse.toDTO(song3),GetSongResponse.toDTO(song1));
		when(playlistRepository.findById(playlistId)).thenReturn(Optional.ofNullable(playlist));
		when(songRepository.findAllByPlaylistOrderByTitle(playlist)).thenReturn(songs);
		// when
		List<GetSongResponse> songsOrderBy = songService.getSongsOrderBy(user, playlistId, Order.TITLE);
		// then
		assertThat(songsOrderBy).isEqualTo(expected);
	}
	@Test
	@DisplayName("가장 최근에 재생된 순으로 정렬된 노래 목록 조회 성공")
	void 가장최근에재생된순으로정렬된노래목록조회_성공(){
		// given
		List<Song> songs = Arrays.asList(song2, song1, song3);
		List<GetSongResponse> expected = Arrays.asList(GetSongResponse.toDTO(song2),GetSongResponse.toDTO(song1),GetSongResponse.toDTO(song3));
		when(playlistRepository.findById(playlistId)).thenReturn(Optional.ofNullable(playlist));
		when(songRepository.findAllByPlaylistOrderByTitle(playlist)).thenReturn(songs);
		// when
		List<GetSongResponse> songsOrderBy = songService.getSongsOrderBy(user, playlistId, Order.TITLE);
		// then
		assertThat(songsOrderBy).isEqualTo(expected);
	}

	@Test
	@DisplayName("정렬된 노래 목록 조회 실패 : 존재하지 않는 플레이리스트")
	void 정렬된노래목록조회_실패_존재하지않는플레이리스트(){
		// given
		when(playlistRepository.findById(playlistId)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> songService.getSongsOrderBy(user,playlistId,Order.TITLE))
			.isInstanceOf(PlaylistPermissionException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.UNAUTHORIZED.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 플레이리스트 입니다.");
	}
	@Test
	@DisplayName("정렬된 노래 목록 조회 실패 : 주인 불일치")
	void 정렬된노래목록조회_실패_주인불일치(){
		// given
		when(playlistRepository.findById(playlistId)).thenReturn(Optional.ofNullable(playlist));
		// when, then
		assertThatThrownBy(() -> songService.getSongsOrderBy(diffUser,playlistId,Order.LAST_PLAYED_TIME))
			.isInstanceOf(PlaylistPermissionException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","플레이리스트의 주인과 사용자가 일치하지 않습니다.");
	}
}