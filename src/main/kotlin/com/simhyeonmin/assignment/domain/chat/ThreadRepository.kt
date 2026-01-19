package com.simhyeonmin.assignment.domain.chat

import com.simhyeonmin.assignment.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * [Thread] 엔티티에 대한 데이터 접근을 처리하는 리포지토리 인터페이스.
 */
interface ThreadRepository : JpaRepository<Thread, Long> {
    /**
     * 주어진 사용자의 가장 최근에 활성화된 스레드를 조회합니다.
     *
     * @param user 조회할 사용자의 [User] 객체.
     * @return 주어진 사용자의 가장 최근에 활성화된 스레드를 포함하는 [Optional], 없으면 빈 [Optional].
     */
    fun findTopByUserOrderByLastActiveAtDesc(user: User): Optional<Thread>

    /**
     * 주어진 사용자와 연관된 모든 스레드를 페이지네이션하여 조회합니다.
     *
     * @param user 조회할 사용자의 [User] 객체.
     * @param pageable 페이지네이션 정보.
     * @return 주어진 사용자와 연관된 [Thread]의 페이지 객체.
     */
    fun findAllByUser(user: User, pageable: Pageable): Page<Thread>

    /**
     * 모든 스레드를 페이지네이션하여 조회합니다. (관리자용)
     *
     * @param pageable 페이지네이션 정보.
     * @return 모든 [Thread]의 페이지 객체.
     */
    override fun findAll(pageable: Pageable): Page<Thread>
}
