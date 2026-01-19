package com.simhyeonmin.assignment.domain.chat

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.Optional

/**
 * [Chat] 엔티티에 대한 데이터 접근을 처리하는 리포지토리 인터페이스.
 */
interface ChatRepository : JpaRepository<Chat, Long> {
    /**
     * 주어진 스레드에 속하는 모든 대화 메시지를 최신순으로 정렬하여 조회합니다.
     *
     * @param thread 조회할 [Thread] 객체.
     * @param pageable 페이지네이션 정보.
     * @return 주어진 스레드에 속하는 대화 메시지 리스트.
     */
    fun findAllByThreadOrderByCreatedAtDesc(thread: Thread, pageable: Pageable): List<Chat>

    /**
     * 특정 스레드에 속한 모든 대화 기록을 삭제합니다.
     *
     * @param thread 삭제할 대화 기록이 속한 [Thread] 객체.
     */
    fun deleteAllByThread(thread: Thread)

    /**
     * 특정 시간 이후에 생성된 모든 대화의 수를 집계합니다.
     *
     * @param dateTime 기준 시간
     * @return 집계된 대화 수
     */
    fun countByCreatedAtAfter(dateTime: LocalDateTime): Long

    /**
     * 특정 시간 이후에 생성된 모든 대화를 연관된 사용자 정보와 함께 조회합니다. (N+1 문제 방지)
     *
     * @param dateTime 기준 시간
     * @return 사용자 정보가 포함된 대화 목록
     */
    @Query("SELECT c FROM Chat c JOIN FETCH c.thread t JOIN FETCH t.user WHERE c.createdAt > :dateTime")
    fun findAllWithUserByCreatedAtAfter(@Param("dateTime") dateTime: LocalDateTime): List<Chat>
}
