package com.simhyeonmin.assignment.domain.chat

import com.simhyeonmin.assignment.global.baseEntity.BaseEntity
import jakarta.persistence.*

/**
 * 대화 기록의 단일 메시지를 나타내는 엔티티 클래스.
 * 사용자의 질문 또는 챗봇의 응답을 저장합니다.
 *
 * @property id 메시지의 고유 식별자 (자동 생성).
 * @property thread 이 메시지가 속한 대화 스레드 ([Thread] 엔티티와의 다대일 관계).
 * @property content 메시지 내용 (질문 또는 응답).
 * @property isUserMessage 이 메시지가 사용자에 의해 생성되었는지 여부 (true: 사용자 메시지, false: 챗봇 응답).
 */
@Entity
@Table(name = "chats")
data class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    val thread: Thread,

    @Column(nullable = false, length = 2000)
    val question: String,

    @Column(length = 2000)
    var answer: String? // AI 응답은 나중에 업데이트될 수 있으므로 nullable

) : BaseEntity() {
    // Lombok의 @Builder를 대체하는 Kotlin의 copy() 및 생성자 활용
}
