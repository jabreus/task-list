package com.demos.tasklist.utils;

import com.demos.tasklist.users.JwtUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class CurrentUserParamResolver implements HandlerMethodArgumentResolver {

  private final JwtTokenUtil jwtUtils;

  private final JwtUserDetailsService userDetailsService;

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.getParameterAnnotation(CurrentUser.class) != null;
  }

  @SuppressWarnings("all")
  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) {

    val request = (HttpServletRequest) webRequest.getNativeRequest();
    val jwt = jwtUtils.parseJwt(request);
    val username = jwtUtils.getUsernameFromToken(jwt);
    return userDetailsService.getByUsername(username);
  }
}
