package com.simhyeonmin.assignment.global.apiPayload.code

import org.springframework.http.HttpStatus

/**
 * 에러 응답을 위한 데이터 전송 객체 (DTO).
 *
 * @property httpStatus HTTP 상태 코드.
 * @property isSuccess 성공 여부 (항상 false).
 * @property code 커스텀 에러 코드.
 * @property message 사용자에게 표시될 에러 메시지.
 */
data class ErrorReasonDTO(
    val httpStatus: HttpStatus,
    val isSuccess: Boolean,
    val code: String,
    val message: String
)
