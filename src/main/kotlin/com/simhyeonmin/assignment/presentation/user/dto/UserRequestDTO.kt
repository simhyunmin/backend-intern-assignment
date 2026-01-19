package com.simhyeonmin.assignment.presentation.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 사용자 요청 데이터 전송 객체 (DTO)들을 포함하는 클래스.
 */
class UserRequestDTO {

    /**
     * 사용자 회원가입 요청을 위한 DTO.
     *
     * @property email 사용자 이메일. 유효한 이메일 형식이어야 하며 비어 있을 수 없습니다.
     * @property password 사용자 비밀번호. 8자 이상 20자 이하여야 하며 비어 있을 수 없습니다.
     * @property name 사용자 이름. 비어 있을 수 없습니다.
     */
    data class SignUp(
        @field:NotBlank(message = "이메일은 필수입니다.")
        @field:Email(message = "유효한 이메일 형식이 아닙니다.")
        @Schema(description = "사용자 이메일", example = "test@example.com")
        val email: String,

        @field:NotBlank(message = "비밀번호는 필수입니다.")
        @field:Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
        @Schema(description = "사용자 비밀번호", example = "password123!")
        val password: String,

        @field:NotBlank(message = "이름은 필수입니다.")
        @Schema(description = "사용자 이름", example = "홍길동")
        val name: String
    )

    /**
     * 사용자 로그인 요청을 위한 DTO.
     *
     * @property email 사용자 이메일. 비어 있을 수 없습니다.
     * @property password 사용자 비밀번호. 비어 있을 수 없습니다.
     */
    data class Login(
        @field:NotBlank(message = "이메일은 필수입니다.")
        @Schema(description = "사용자 이메일", example = "test@example.com")
        val email: String,

        @field:NotBlank(message = "비밀번호는 필수입니다.")
        @Schema(description = "사용자 비밀번호", example = "password123!")
        val password: String
    )
}
