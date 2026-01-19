package com.simhyeonmin.assignment.domain.feedback

import com.simhyeonmin.assignment.domain.chat.Chat
import com.simhyeonmin.assignment.domain.user.User
import com.simhyeonmin.assignment.global.baseEntity.BaseEntity
import com.simhyeonmin.assignment.global.enums.FeedbackStatus
import jakarta.persistence.*

/**
 * 대화(Chat)에 대한 사용자 피드백을 나타내는 엔티티.
 *
 * 한 명의 사용자는 하나의 대화에 대해 하나의 피드백만 남길 수 있도록
 * user_id와 chat_id에 복합 유니크 제약 조건을 설정합니다.
 */
@Entity
@Table(
    name = "feedbacks",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_user_chat_feedback",
            columnNames = ["user_id", "chat_id"]
        )
    ]
)
class Feedback(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    val chat: Chat,

    @Column(nullable = false)
    var positive: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: FeedbackStatus = FeedbackStatus.PENDING

) : BaseEntity()
