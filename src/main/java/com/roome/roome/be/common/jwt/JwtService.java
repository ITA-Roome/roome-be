package com.roome.roome.be.common.jwt;

import com.roome.roome.be.common.exception.GeneralException;
import com.roome.roome.be.common.status.ErrorStatus;
import com.roome.roome.be.domain.user.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtService {

    private final Key secretKey;
    @Getter
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("nickname", user.getNickname())
                .claim("loginType", user.getLoginType().name())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /** Access Token 검증 */
    public void validateJwtToken(String token) {
        if (token == null || token.isBlank()) {
            throw new GeneralException(ErrorStatus.JWT_TOKEN_NOT_FOUND);
        }
        Claims claims = getClaims(token);
    }

    /** Refresh Token 검증 */
    public Claims validateRefreshToken(String token) {
        return getClaims(token);
    }

    /** JWT 토큰에서 userId 추출 */
    public Long getUserIdFromJwtToken(String token) {
        try {
            return Long.parseLong(
                    Jwts.parserBuilder()
                            .setSigningKey(secretKey)
                            .build()
                            .parseClaimsJws(token)
                            .getBody()
                            .getSubject()
            );
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus.JWT_EXTRACT_ID_FAILED);
        }
    }

    /** 내부 Claims 파싱 로직, 만료된 토큰, 서명 불일치 등 예외 시 GeneralException 처리 */
    private Claims getClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (SecurityException ex) {
            throw new GeneralException(ErrorStatus.JWT_INVALID_SIGNATURE);

        } catch (MalformedJwtException ex) {
            throw new GeneralException(ErrorStatus.JWT_MALFORMED);

        } catch (ExpiredJwtException ex) {
            throw new GeneralException(ErrorStatus.JWT_EXPIRED);

        } catch (UnsupportedJwtException ex) {
            throw new GeneralException(ErrorStatus.JWT_UNSUPPORTED);

        } catch (IllegalArgumentException ex) {
            throw new GeneralException(ErrorStatus.JWT_INVALID);

        } catch (Exception ex) {
            throw new GeneralException(ErrorStatus.JWT_GENERAL_ERROR);
        }
    }
}
