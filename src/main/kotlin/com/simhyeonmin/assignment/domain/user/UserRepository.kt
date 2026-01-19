package com.simhyeonmin.assignment.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.Optional

/**
 * [User] 엔티티에 대한 데이터 접근을 처리하는 리포지토리 인터페이스.
 */
interface UserRepository : JpaRepository<User, Long> {
    /**
     * 이메일을 통해 사용자를 조회합니다.
     *
     * @param email 조회할 사용자의 이메일 주소.
     * @return 주어진 이메일에 해당하는 [User] 객체를 포함하는 [Optional], 없으면 빈 [Optional].
     */
    fun findByEmail(email: String): Optional<User>

    /**
     * 특정 시간 이후에 가입한 사용자의 수를 집계합니다.
     *
     * @param dateTime 기준 시간
     * @return 집계된 사용자 수
     */
    fun countByCreatedAtAfter(dateTime: LocalDateTime): Long
}
