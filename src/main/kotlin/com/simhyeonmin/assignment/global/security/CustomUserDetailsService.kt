package com.simhyeonmin.assignment.global.security

import com.simhyeonmin.assignment.domain.user.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.Collections

/**
 * Spring Security에서 사용자 정보를 로드하는 서비스.
 * [UserRepository]를 사용하여 사용자 ID로 [UserDetails]를 조회합니다.
 *
 * @property userRepository 사용자 데이터 접근을 위한 리포지토리.
 */
@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    /**
     * 주어진 사용자 이름(여기서는 사용자 ID)으로 [UserDetails]를 로드합니다.
     *
     * @param username 로드할 사용자의 ID (문자열 형태).
     * @return 로드된 [UserDetails] 객체 ([CustomUserDetails]).
     * @throws UsernameNotFoundException 해당 사용자 ID를 가진 사용자를 찾을 수 없을 경우.
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val userId = username.toLongOrNull()
            ?: throw UsernameNotFoundException("유효하지 않은 사용자 ID 형식입니다: $username")

        val user = userRepository.findById(userId)
            .orElseThrow { UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. ID: $userId") }

        println("====================================================")
        println("AUTHORITY CHECK")
        println("Loading user with ID: $userId")
        println("User's role from DB: ${user.role.name}")
        println("====================================================")

        val authorities = Collections.singleton(SimpleGrantedAuthority("ROLE_${user.role.name}"))

        return CustomUserDetails(user.id!!, authorities)
    }
}
