package com.roome.roome.be.common.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roome.roome.be.common.base.BaseStatus;
import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.response.ApiResponse;
import com.roome.roome.be.common.status.ErrorStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwt = resolveToken(request);

            if (jwt != null) {
                jwtService.validateJwtToken(jwt);
                Long userId = jwtService.getUserIdFromJwtToken(jwt);
                String role = jwtService.getRoleFromJwtToken(jwt);

                List<SimpleGrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_" + role));

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (GeneralException e) {
            handleGeneralJwtError(e.getErrorStatus(), response);
        } catch (Exception e) {
            handleJwtError(e.getMessage(), response);
        }
    }

    /**
     * Authorization 헤더에서 JWT 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * JWT 관련 커스텀 예외 처리
     */
    private void handleGeneralJwtError(BaseStatus errorStatus, HttpServletResponse response) throws IOException {
        log.error("[*] GeneralException in JWT Filter → {}", errorStatus.getMessage());
        ApiResponse<?> errorResponse = ApiResponse.error(errorStatus).getBody();
        setHttpServletResponse(errorStatus.getHttpStatus().value(), errorResponse, response);
    }

    /**
     * 내부 시스템 예외 처리
     */
    private void handleJwtError(String msg, HttpServletResponse response) throws IOException {
        log.error("[*] Internal Exception in JWT Filter → {}", msg);
        ErrorStatus errorStatus = ErrorStatus.INTERNAL_SERVER_ERROR;
        ApiResponse<?> errorResponse =
                ApiResponse.error(errorStatus, msg != null ? msg : errorStatus.getMessage()).getBody();
        setHttpServletResponse(errorStatus.getHttpStatus().value(), errorResponse, response);
    }

    /**
     * HTTP 응답 JSON 포맷 설정
     */
    private void setHttpServletResponse(int status, ApiResponse<?> errorResponse, HttpServletResponse response)
            throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
