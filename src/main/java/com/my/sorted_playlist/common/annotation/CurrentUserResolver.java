package com.my.sorted_playlist.common.annotation;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.my.sorted_playlist.common.exception.UserAccessException;
import com.my.sorted_playlist.user.domain.User;
import com.my.sorted_playlist.user.dto.UserResponse;
import com.my.sorted_playlist.user.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

	private final HttpSession httpSession;
	private final UserRepository userRepository;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class)
			&& parameter.getParameterType().equals(User.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
		UserResponse userResponse = (UserResponse) httpSession.getAttribute("loginUser");
		if (userResponse == null) throw new UserAccessException("잘못된 세션 정보입니다.");
		return userRepository.findByEmail(userResponse.email()).orElseThrow(() -> new UserAccessException("exception"));
	}
}
