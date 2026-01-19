package com.simhyeonmin.assignment.global.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.Collections
import org.springframework.security.core.authority.SimpleGrantedAuthority

/**
 * Spring Security에서 사용자 정보를 나타내는 구현체.
 *
 * @property userId 인증된 사용자의 고유 ID.
 */
class CustomUserDetails(
    val userId: Long,
    private val authorities: Collection<GrantedAuthority>
) : UserDetails {

    /**
     * 사용자가 가지고 있는 권한 목록을 반환합니다.
     * 생성 시 주입된 권한 목록을 반환합니다.
     *
     * @return 권한 목록.
     */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    /**
     * 사용자의 비밀번호를 반환합니다.
     * JWT 인증에서는 비밀번호를 직접 사용하지 않으므로 null을 반환합니다.
     *
     * @return 항상 null.
     */
    override fun getPassword(): String? {
        return null
    }

    /**
     * 사용자의 사용자 이름을 반환합니다. 여기서는 userId를 문자열로 변환하여 사용합니다.
     *
     * @return 사용자의 userId 문자열.
     */
    override fun getUsername(): String {
        return userId.toString()
    }

    /**
     * 계정 만료 여부를 반환합니다.
     *
     * @return 항상 true.
     */
    override fun isAccountNonExpired(): Boolean {
        return true
    }

    /**
     * 계정 잠금 여부를 반환합니다.
     *
     * @return 항상 true.
     */
    override fun isAccountNonLocked(): Boolean {
        return true
    }

    /**
     * 자격 증명(비밀번호) 만료 여부를 반환합니다.
     *
     * @return 항상 true.
     */
    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    /**
     * 계정 활성화 여부를 반환합니다.
     *
     * @return 항상 true.
     */
    override fun isEnabled(): Boolean {
        return true
    }
}
