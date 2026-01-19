package com.simhyeonmin.assignment.domain.user

import com.simhyeonmin.assignment.global.baseEntity.BaseEntity
import com.simhyeonmin.assignment.global.enums.UserRole
import jakarta.persistence.*

/**
 * 애플리케이션 사용자를 나타내는 엔티티 클래스.
 *
 * @property id 사용자의 고유 식별자 (자동 생성).
 * @property email 사용자의 이메일 주소 (고유).
 * @property password 사용자의 비밀번호 (해싱된 값).
 * @property name 사용자의 이름.
 * @property role 사용자의 역할 ([UserRole.MEMBER] 또는 [UserRole.ADMIN]).
 */
@Entity
@Table(name = "users") // 'user'는 SQL 예약어일 수 있으므로 'users'로 사용
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var password: String, // 실제 사용 시 BCrypt 등으로 해싱 필요

    @Column(nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole

) : BaseEntity() {
    // Lombok의 @Builder를 대체하는 Kotlin의 copy() 및 생성자 활용
    // 필요한 경우 추가적인 비즈니스 로직 메서드 구현
}
