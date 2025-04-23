package com.delivery.igo.igo_delivery.common.filter;

import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import com.delivery.igo.igo_delivery.common.exception.ErrorDto;
import com.delivery.igo.igo_delivery.common.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final List<String> WHITE_LIST = List.of("/auth/signup", "/auth/login");

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String url = request.getRequestURI();

        if (isWhiteList(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String bearerJwt = request.getHeader("Authorization");

        if (bearerJwt == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "JWT 토큰이 필요합니다.");
            return;
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        try {
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                setErrorResponse(response, ErrorCode.JWT_BAD_TOKEN, url);
                return;
            }

            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

            request.setAttribute("userId", Long.parseLong(claims.getSubject()));
            request.setAttribute("email", claims.get("email"));
            request.setAttribute("userRole", claims.get("userRole"));

            if (url.startsWith("/admin") && !UserRole.ADMIN.equals(userRole)) {
                setErrorResponse(response, ErrorCode.ROLE_ADMIN_FORBIDDEN, url);
                return;
            }

            filterChain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature", e);
            setErrorResponse(response, ErrorCode.JWT_INVALID_SIGNATURE, url);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
            setErrorResponse(response, ErrorCode.JWT_EXPIRED, url);
        } catch (Exception e) {
            log.error("Invalid JWT token", e);
            setErrorResponse(response, ErrorCode.JWT_INVALID_TOKEN, url);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorCode errorCode, String path) throws IOException {
        ErrorDto errorDto = new ErrorDto(
                errorCode.getHttpStatus().value(),
                errorCode.getMessage(),
                LocalDateTime.now(),
                path
        );

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        response.getWriter().write(mapper.writeValueAsString(errorDto));
    }

    private boolean isWhiteList(String uri) {
        return WHITE_LIST.stream().anyMatch(uri::startsWith);
    }
}
