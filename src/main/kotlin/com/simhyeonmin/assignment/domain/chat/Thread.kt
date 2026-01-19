package com.simhyeonmin.assignment.domain.chat

import com.simhyeonmin.assignment.domain.user.User
import com.simhyeonmin.assignment.global.baseEntity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * 사용자별 대화 스레드를 나타내는 엔티티 클래스.
 * 각 스레드는 사용자의 대화 기록을 그룹화하며, 특정 시간(30분) 내에 재활성화될 수 있습니다.
 *
 * @property id 스레드의 고유 식별자 (자동 생성).
 * @property user 이 스레드를 생성한 사용자 ([User] 엔티티와의 다대일 관계).
 * @property lastActiveAt 스레드 내 마지막 활동 시간.
 */
@Entity
@Table(name = "threads")
data class Thread(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(name = "last_active_at", nullable = false)
    var lastActiveAt: LocalDateTime = LocalDateTime.now()

) : BaseEntity() {
    /**
     * 스레드의 마지막 활동 시간을 현재 시간으로 업데이트합니다.
     */
    fun updateLastActiveAt() {
        this.lastActiveAt = LocalDateTime.now()
    }
}
