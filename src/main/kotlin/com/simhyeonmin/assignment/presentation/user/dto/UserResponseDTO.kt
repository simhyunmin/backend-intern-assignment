package com.simhyeonmin.assignment.presentation.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 사용자 응답 데이터 전송 객체 (DTO)들을 포함하는 클래스.
 */
class UserResponseDTO {

    /**
     * 사용자 회원가입 응답을 위한 DTO.
     *
     * @property userId 새로 생성된 사용자의 ID.
     * @property email 사용자 이메일.
     * @property name 사용자 이름.
     */
    data class SignUp(
        @Schema(description = "새로 생성된 사용자 ID", example = "1")
        val userId: Long,
        @Schema(description = "사용자 이메일", example = "test@example.example")
        val email: String,
        @Schema(description = "사용자 이름", example = "홍길동")
        val name: String
    )

    /**
     * 사용자 로그인 응답을 위한 DTO.
     *
     * @property userId 로그인한 사용자의 ID.
     * @property accessToken 발급된 액세스 토큰.
     * @property refreshToken 발급된 리프레시 토큰.
     * @property accessTokenExpiresIn 액세스 토큰 만료 시간 (LocalDateTime).
     */
    data class Login(
        @Schema(description = "로그인한 사용자 ID", example = "1")
        val userId: Long,
        @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiI...")
        val accessToken: String,
        @Schema(description = "리프레시 토큰", example = "eyJhbGciOiJIUzI1NiI...")
        val refreshToken: String,
        @Schema(description = "액세스 토큰 만료 시간", example = "2026-01-01T12:00:00")
        val accessTokenExpiresIn: LocalDateTime
    )
}
