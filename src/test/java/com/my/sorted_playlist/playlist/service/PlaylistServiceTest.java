package com.my.sorted_playlist.playlist.service;

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
import com.my.sorted_playlist.playlist.dto.EditPlaylistNameRequest;
import com.my.sorted_playlist.playlist.dto.GetPlaylistResponse;
import com.my.sorted_playlist.playlist.exception.PlaylistPermissionException;
import com.my.sorted_playlist.playlist.exception.PlaylistRequestException;
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
	private final String newName = "newName";
	private EditPlaylistNameRequest editRequest;
	private User user;
	private User diffUser;
	private Playlist playlist;
	private Playlist playlist2;
	@BeforeEach
	void set() throws NoSuchFieldException, IllegalAccessException {
		user = User.builder().email("email").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();
		diffUser = User.builder().email("anotherName").password("password").nickname("nickname").profileImage("image").role(Role.USER).build();
		playlist = Playlist.builder().name(playListName).user(user).createdDateTime(LocalDateTime.now()).songCount(0).build();
		playlist2 = Playlist.builder().name(newName).user(user).createdDateTime(LocalDateTime.now()).songCount(0).build();
		editRequest = new EditPlaylistNameRequest(1L,newName);


		Field userIdField = User.class.getDeclaredField("id");
		userIdField.setAccessible(true);
		userIdField.set(user, 1L);

		Field userIdField2 = User.class.getDeclaredField("id");
		userIdField2.setAccessible(true);
		userIdField2.set(diffUser, 2L);

		Field playlistIdField = Playlist.class.getDeclaredField("id");
		playlistIdField.setAccessible(true);
		playlistIdField.set(playlist, 10L);

		Field playlistIdField2 = Playlist.class.getDeclaredField("id");
		playlistIdField2.setAccessible(true);
		playlistIdField2.set(playlist2, 11L);
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

	@Test
	@DisplayName("플레이리스트 생성 실패 : 중복되는 이름의 플레이리스트 존재")
	void 플레이리스트생성_실패_중복이름(){
		// given
		when(playListRepository.existsByUserAndName(user,playListName)).thenReturn(true);
		// when, then
		assertThatThrownBy(() -> playListService.createPlayList(user,playListName))
			.isInstanceOf(PlaylistRequestException.class)
			.hasFieldOrPropertyWithValue("error","중복되는 이름의 플레이리스트가 있습니다.");
	}

	@Test
	@DisplayName("플레이리스트 이름 수정 성공")
	void 플레이리스트이름수정_성공(){
		// given
		when(playListRepository.findById(editRequest.playlistId())).thenReturn(Optional.ofNullable(playlist));
		// when
		Playlist edited = playListService.editPlayListName(user, editRequest);
		// then
		assertThat(edited.getName()).isEqualTo(newName);
	}

	@Test
	@DisplayName("플레이리스트 이름 수정 실패 : 존재하지 않는 플레이리스트")
	void 플레이리스트이름수정_실패_존재하지않는플레이리스트(){
		// given
		when(playListRepository.findById(editRequest.playlistId())).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> playListService.editPlayListName(user,editRequest))
			.isInstanceOf(PlaylistPermissionException.class)
			.hasFieldOrPropertyWithValue("status", HttpStatus.UNAUTHORIZED.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 플레이리스트 입니다.");
	}

	@Test
	@DisplayName("플레이리스트 이름 수정 실패 : 플레이리스트의 주인 불일치")
	void 플레이리스트이름수정_실패_플레이리스트주인불일치() throws NoSuchFieldException, IllegalAccessException {
		// given
		when(playListRepository.findById(editRequest.playlistId())).thenReturn(Optional.ofNullable(playlist));
		// when, then
		assertThatThrownBy(() -> playListService.editPlayListName(diffUser,editRequest))
			.isInstanceOf(PlaylistPermissionException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","플레이리스트의 주인과 사용자가 일치하지 않습니다.");
	}

	@Test
	@DisplayName("플레이리스트 전체 조회 성공")
	void 플레이리스트전체조회_성공(){
		// given
		List<Playlist> playlists = Arrays.asList(playlist, playlist2);
		when(playListRepository.findAllByUser(user)).thenReturn(playlists);
		// when
		List<GetPlaylistResponse> result = playListService.getPlayLists(user);
		// then
		verify(playListRepository,times(1)).findAllByUser(user);
		assertThat(result).hasSize(2);
	}

	@Test
	@DisplayName("플레이리스트 삭제 성공")
	void 플레이리스트삭제_성공(){
		// given
		when(playListRepository.findById(10L)).thenReturn(Optional.ofNullable(playlist));
		// when
		playListService.deletePlaylist(user,10L);
		// then
		verify(playListRepository,times(1)).deleteById(10L);
	}

	@Test
	@DisplayName("플레이리스트 삭제 실패 : 존재하지 않는 플레이리스트")
	void 플레이리스트삭제_실패_존재하지않는플레이리스트(){
		// given
		when(playListRepository.findById(10L)).thenReturn(Optional.empty());
		// when, then
		assertThatThrownBy(() -> playListService.deletePlaylist(user, 10L))
			.isInstanceOf(PlaylistPermissionException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.UNAUTHORIZED.value())
			.hasFieldOrPropertyWithValue("error","존재하지 않는 플레이리스트 입니다.");
	}

	@Test
	@DisplayName("플레이리스트 삭제 실패 : 플레이리스트의 주인 불일치")
	void 플레이리스트삭제_실패_플레이리스트주인불일치(){
		// given
		when(playListRepository.findById(10L)).thenReturn(Optional.ofNullable(playlist));
		// when, then
		assertThatThrownBy(() -> playListService.deletePlaylist(diffUser,10L))
			.isInstanceOf(PlaylistPermissionException.class)
			.hasFieldOrPropertyWithValue("status",HttpStatus.FORBIDDEN.value())
			.hasFieldOrPropertyWithValue("error","플레이리스트의 주인과 사용자가 일치하지 않습니다.");
	}

}