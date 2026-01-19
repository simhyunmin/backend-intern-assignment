package com.simhyeonmin.assignment.domain.feedback

import com.simhyeonmin.assignment.domain.chat.Chat
import com.simhyeonmin.assignment.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * [Feedback] 엔티티에 대한 데이터 접근을 처리하는 리포지토리 인터페이스.
 * JpaSpecificationExecutor를 상속하여 동적 쿼리 생성을 지원합니다.
 */
interface FeedbackRepository : JpaRepository<Feedback, Long>, JpaSpecificationExecutor<Feedback> {

    /**
     * 특정 사용자가 특정 대화에 대해 피드백을 이미 생성했는지 확인합니다.
     *
     * @param user 확인할 사용자
     * @param chat 확인할 대화
     * @return 피드백이 존재하면 true, 그렇지 않으면 false
     */
    fun existsByUserAndChat(user: User, chat: Chat): Boolean
}
