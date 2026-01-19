package com.simhyeonmin.assignment.presentation.user

import com.simhyeonmin.assignment.presentation.user.dto.UserRequestDTO
import com.simhyeonmin.assignment.presentation.user.dto.UserResponseDTO
import com.simhyeonmin.assignment.domain.user.service.UserService

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.simhyeonmin.assignment.global.response.ApiResponse

/**
 * 사용자 관련 API 엔드포인트를 제공하는 컨트롤러.
 * 회원가입 및 로그인 기능을 처리합니다.
 *
 * @property userService 사용자 비즈니스 로직을 처리하는 서비스.
 */
@RestController
@RequestMapping("/members") // 기존 @Spring 프로젝트의 패턴을 따름
@Tag(name = "User", description = "사용자 관련 API")
class UserController(
    private val userService: UserService
) {

    /**
     * 사용자 회원가입을 처리하는 엔드포인트.
     *
     * @param request 회원가입 요청 DTO ([UserRequestDTO.SignUp]).
     * @return 성공 시 생성된 사용자 정보를 포함하는 [ApiResponse] 응답.
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    fun signUp(@RequestBody @Valid request: UserRequestDTO.SignUp): ResponseEntity<ApiResponse<UserResponseDTO.SignUp>> {
        val response = userService.signUp(request)
        return ResponseEntity.ok(ApiResponse.onSuccess(response))
    }

    /**
     * 사용자 로그인을 처리하고 JWT 토큰을 발급하는 엔드포인트.
     *
     * @param request 로그인 요청 DTO ([UserRequestDTO.Login]).
     * @return 성공 시 발급된 토큰 정보를 포함하는 [ApiResponse] 응답.
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호를 사용하여 로그인하고 JWT 토큰을 발급받습니다.")
    fun login(@RequestBody @Valid request: UserRequestDTO.Login): ResponseEntity<ApiResponse<UserResponseDTO.Login>> {
        val response = userService.login(request)
        return ResponseEntity.ok(ApiResponse.onSuccess(response))
    }
}
