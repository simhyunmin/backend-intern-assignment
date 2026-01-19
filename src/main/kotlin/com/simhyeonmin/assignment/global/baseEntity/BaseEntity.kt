package com.simhyeonmin.assignment.global.baseEntity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * 모든 엔티티가 상속받는 기본 엔티티 클래스.
 * 생성일시 ([createdAt])와 수정일시 ([updatedAt]) 필드를 자동으로 관리합니다.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set // Setter는 protected로 하여 외부에서 직접 변경 불가하도록

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set // Setter는 protected로 하여 외부에서 직접 변경 불가하도록
}
