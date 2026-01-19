package com.simhyeonmin.assignment.global.apiPayload.code

import org.springframework.http.HttpStatus

/**
 * 일반적인 응답을 위한 데이터 전송 객체 (DTO).
 * 성공 및 에러 응답의 기본 구조를 제공합니다.
 *
 * @property httpStatus HTTP 상태 코드.
 * @property isSuccess 성공 여부.
 * @property code 응답 코드.
 * @property message 응답 메시지.
 */
data class ReasonDTO(
    val httpStatus: HttpStatus,
    val isSuccess: Boolean,
    val code: String,
    val message: String
)
