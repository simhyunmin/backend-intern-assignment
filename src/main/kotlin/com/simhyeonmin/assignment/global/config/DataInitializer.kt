package com.simhyeonmin.assignment.global.config

import com.simhyeonmin.assignment.domain.user.User
import com.simhyeonmin.assignment.domain.user.UserRepository
import com.simhyeonmin.assignment.global.enums.UserRole
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 애플리케이션 구동 시 초기 데이터를 설정하여, 즉시 실행 및 테스트 가능한 상태를 보장합니다.
 * 별도의 데이터베이스 설정이나 수동 데이터 입력 없이도 핵심 기능을 검증할 수 있도록 지원합니다.
 */
@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    private val adminEmail = "admin@example.com"
    private val adminPassword = "admin1234"

    @EventListener(ApplicationReadyEvent::class)
    @Transactional
    fun initAdminUser() {
        // 원활한 시연 및 기능 검증을 위해 애플리케이션 구동 시 관리자 계정을 초기화합니다.
        if (userRepository.findByEmail(adminEmail).isEmpty) {
            val adminUser = User(
                email = adminEmail,
                password = passwordEncoder.encode(adminPassword),
                name = "Admin",
                role = UserRole.ADMIN
            )
            userRepository.save(adminUser)
            println("Admin user for demo has been created: $adminEmail")
        }
    }
}
