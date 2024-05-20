package com.my.sorted_playlist.common.annotation;

import java.util.Objects;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.my.sorted_playlist.common.exception.UserAccessException;
import com.my.sorted_playlist.user.domain.User;
import com.my.sorted_playlist.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

	private final UserRepository userRepository;

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CurrentUser.class)
			&& parameter.getParameterType().equals(User.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory){
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		HttpSession httpSession = Objects.requireNonNull(request).getSession(true);
		SessionInfo sessionInfo = (SessionInfo) httpSession.getAttribute("sessionInfo");
		if (sessionInfo == null) throw new UserAccessException("잘못된 세션 정보입니다.");
		Long userId = sessionInfo.getId();
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserAccessException("존재하지 않는 유저입니다."));
	}
}
