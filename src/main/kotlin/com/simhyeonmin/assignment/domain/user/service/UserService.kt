package com.simhyeonmin.assignment.domain.user.service

import com.simhyeonmin.assignment.domain.user.User
import com.simhyeonmin.assignment.domain.user.UserRepository
import com.simhyeonmin.assignment.presentation.user.dto.UserRequestDTO
import com.simhyeonmin.assignment.presentation.user.dto.UserResponseDTO
import com.simhyeonmin.assignment.global.apiPayload.code.status.ErrorStatus
import com.simhyeonmin.assignment.global.apiPayload.exception.handler.MemberHandler
import com.simhyeonmin.assignment.global.enums.UserRole
import com.simhyeonmin.assignment.global.jwt.JwtProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스.
 * 회원가입, 로그인 등의 기능을 제공합니다.
 *
 * @property userRepository 사용자 데이터 접근을 위한 리포지토리.
 * @property passwordEncoder 비밀번호 암호화를 위한 인코더.
 * @property jwtProvider JWT 토큰 생성 및 관리를 위한 프로바이더.
 */
@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider
) {

    /**
     * 새로운 사용자를 등록합니다.
     *
     * @param request 회원가입 요청 DTO ([UserRequestDTO.SignUp]).
     * @return 등록된 사용자 정보를 포함하는 응답 DTO ([UserResponseDTO.SignUp]).
     * @throws MemberHandler 이메일이 이미 존재하는 경우.
     */
    @Transactional
    fun signUp(request: UserRequestDTO.SignUp): UserResponseDTO.SignUp {
        if (userRepository.findByEmail(request.email).isPresent) {
            throw MemberHandler(ErrorStatus.MEMBER_ALREADY_EXISTS)
        }

        val encodedPassword = passwordEncoder.encode(request.password)
        val newUser = User(
            email = request.email,
            password = encodedPassword,
            name = request.name,
            role = UserRole.MEMBER // 기본 역할은 MEMBER
        )
        val savedUser = userRepository.save(newUser)

        val newUserId = savedUser.id ?: throw IllegalStateException("Saved User ID cannot be null.")

        return UserResponseDTO.SignUp(
            userId = newUserId,
            email = savedUser.email,
            name = savedUser.name
        )
    }

    /**
     * 사용자 로그인을 처리하고 JWT 토큰을 발급합니다.
     *
     * @param request 로그인 요청 DTO ([UserRequestDTO.Login]).
     * @return 발급된 토큰 정보를 포함하는 응답 DTO ([UserResponseDTO.Login]).
     * @throws MemberHandler 사용자를 찾을 수 없거나 비밀번호가 일치하지 않는 경우.
     */
    @Transactional
    fun login(request: UserRequestDTO.Login): UserResponseDTO.Login {
        val user = userRepository.findByEmail(request.email)
            .orElseThrow { MemberHandler(ErrorStatus.MEMBER_NOT_FOUND) }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw MemberHandler(ErrorStatus.INVALID_MEMBER_INFO) // 비밀번호 불일치 에러
        }

        // Safely get user ID
        val userId = user.id ?: throw IllegalStateException("User ID cannot be null after login query.")

        val accessToken = jwtProvider.generateAccessToken(userId)
        val refreshToken = jwtProvider.generateRefreshToken(userId)
        val accessTokenExpiresIn = jwtProvider.getExpiredAt(accessToken)

        // TODO: 리프레시 토큰은 DB에 저장하여 관리하는 것이 일반적이지만, MVP를 위해 생략합니다.

        return UserResponseDTO.Login(
            userId = userId, // Use the non-nullable local variable
            accessToken = accessToken,
            refreshToken = refreshToken,
            accessTokenExpiresIn = accessTokenExpiresIn
        )
    }
}
