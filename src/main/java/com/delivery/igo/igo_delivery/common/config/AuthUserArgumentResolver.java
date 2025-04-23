//package com.delivery.igo.igo_delivery.common.config;
//
//import com.delivery.igo.igo_delivery.common.annotation.Auth;
//import com.delivery.igo.igo_delivery.common.dto.AuthUser;
//import jakarta.security.auth.message.AuthException;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.MethodParameter;
//import org.springframework.lang.Nullable;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.support.WebDataBinderFactory;
//import org.springframework.web.context.request.NativeWebRequest;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.method.support.ModelAndViewContainer;
//
//@Component
//@RequiredArgsConstructor
//public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
//
//    private final JwtUtil jwtUtil;
//
//    @Override
//    public boolean supportsParameter(MethodParameter parameter) {
//        boolean hasAuthAnnotation = parameter.getParameterAnnotation(Auth.class) != null;
//        boolean isAuthUserType = parameter.getParameterType().equals(AuthUser.class);
//
//        // @Auth 어노테이션과 AuthUser 타입이 함께 사용되지 않은 경우 예외 발생
//        if (hasAuthAnnotation != isAuthUserType) {
//            throw new AuthException("@Auth와 AuthUser 타입은 함께 사용되어야 합니다.");
//        }
//
//        return hasAuthAnnotation;
//    }
//
//    @Override
//    public Object resolveArgument(
//            @Nullable MethodParameter parameter,
//            @Nullable ModelAndViewContainer mavContainer,
//            NativeWebRequest webRequest,
//            @Nullable WebDataBinderFactory binderFactory
//    ) {
//        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
//
////        // JwtFilter 에서 set 한 userId, email, userRole 값을 가져옴
////        Long userId = (Long) request.getAttribute("userId");
////        String email = (String) request.getAttribute("email");
////        UserRole userRole = UserRole.of((String) request.getAttribute("userRole"));
////
////        return new AuthUser(userId, email, userRole);
//
//        /// jwtUtil에서 token을 활용하는 방식으로 변경하기(refactoring)
//        String token = jwtUtil.substringToken(request.getHeader("Authorization"));
//
//        return new AuthUser(jwtUtil.getUserId(token), jwtUtil.getUserEmail(token), jwtUtil.getUserRole(token));
//    }
//}