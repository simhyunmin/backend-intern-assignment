package com.simhyeonmin.assignment.global.jwt

import com.simhyeonmin.assignment.domain.user.UserRepository
import com.simhyeonmin.assignment.global.apiPayload.code.status.ErrorStatus
import com.simhyeonmin.assignment.global.apiPayload.exception.handler.JwtHandler
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * JWT(JSON Web Token)를 생성하고 유효성을 검증하는 유틸리티 클래스.
 *
 * @property secret JWT 서명에 사용될 비밀 키.
 * @property accessTokenExpiration 액세스 토큰의 만료 시간 (밀리초).
 * @property refreshTokenExpiration 리프레시 토큰의 만료 시간 (밀리초).
 * @property userRepository 사용자 정보를 조회하기 위한 리포지토리.
 */
@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secret: String,
    @Value("\${jwt.access-token-expiration}")
    private val accessTokenExpiration: Long,
    @Value("\${jwt.refresh-token-expiration}")
    private val refreshTokenExpiration: Long,
    private val userRepository: UserRepository
) {
    private lateinit var key: Key

    /**
     * 비밀 키를 초기화합니다.
     * Base64 디코딩 및 HMAC-SHA 키 생성을 수행합니다.
     */
    @PostConstruct
    fun init() {
        val keyBytes = Base64.getEncoder().encode(secret.toByteArray())
        this.key = Keys.hmacShaKeyFor(keyBytes)
    }

    /**
     * Access Token을 생성합니다.
     *
     * @param userId 토큰에 포함될 사용자 ID.
     * @return 생성된 Access Token.
     */
    fun generateAccessToken(userId: Long): String {
        val now = Date()
        val expiry = Date(now.time + accessTokenExpiration)

        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * Refresh Token을 생성합니다.
     *
     * @param userId 토큰에 포함될 사용자 ID.
     * @return 생성된 Refresh Token.
     */
    fun generateRefreshToken(userId: Long): String {
        val now = Date()
        val expiry = Date(now.time + refreshTokenExpiration)

        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    /**
     * JWT 토큰의 유효성을 검증합니다.
     *
     * @param token 검증할 JWT 토큰.
     * @return 토큰이 유효하면 true.
     * @throws JwtHandler 토큰이 만료되었거나 유효하지 않을 경우.
     */
    fun validateToken(token: String): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
            return true
        } catch (e: ExpiredJwtException) {
            throw JwtHandler(ErrorStatus.EXPIRED_JWT_TOKEN)
        } catch (e: Exception) {
            throw JwtHandler(ErrorStatus.INVALID_JWT_TOKEN)
        }
    }

    /**
     * Authorization 헤더에서 "Bearer " 접두사를 제거하여 실제 토큰을 추출합니다.
     *
     * @param bearerHeader "Bearer " 접두사가 포함된 Authorization 헤더 값.
     * @return 추출된 토큰 문자열, 또는 null (접두사가 없거나 헤더가 null인 경우).
     */
    fun resolveToken(bearerHeader: String?): String? {
        return if (bearerHeader != null && bearerHeader.startsWith("Bearer ")) {
            bearerHeader.substring(7)
        } else null
    }

    /**
     * JWT 토큰에서 사용자 ID를 추출합니다.
     *
     * @param token 사용자 ID를 추출할 JWT 토큰.
     * @return 추출된 사용자 ID.
     * @throws JwtHandler 토큰이 유효하지 않을 경우.
     */
    fun getUserId(token: String): Long {
        return try {
            val claims: Claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
            claims.subject.toLong()
        } catch (e: Exception) {
            throw JwtHandler(ErrorStatus.INVALID_JWT_TOKEN)
        }
    }

    /**
     * JWT 토큰에서 만료 시간을 추출합니다.
     *
     * @param token 만료 시간을 추출할 JWT 토큰.
     * @return 토큰의 만료 시간 ([LocalDateTime] 객체).
     * @throws JwtHandler 토큰이 유효하지 않을 경우.
     */
    fun getExpiredAt(token: String): LocalDateTime {
        return try {
            val claims: Claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
            val expiration: Date = claims.expiration
            LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault())
        } catch (e: Exception) {
            throw JwtHandler(ErrorStatus.INVALID_JWT_TOKEN)
        }
    }
}
