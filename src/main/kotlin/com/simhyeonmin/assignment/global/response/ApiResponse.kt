package com.simhyeonmin.assignment.global.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.simhyeonmin.assignment.global.apiPayload.code.BaseCode
import com.simhyeonmin.assignment.global.apiPayload.code.status.SuccessStatus

/**
 * API 응답의 표준 형식을 정의하는 데이터 클래스.
 * 모든 API 응답은 이 형식으로 래핑됩니다.
 *
 * @property isSuccess API 호출의 성공 여부 (true: 성공, false: 실패).
 * @property code 응답 코드 (성공: "COMMON200", 실패: 에러 코드).
 * @property message 응답 메시지.
 * @property result 실제 데이터 (성공 시에만 포함되며, 실패 시에는 null).
 */
@JsonPropertyOrder("isSuccess", "code", "message", "result")
data class ApiResponse<T>(
    @JsonProperty("isSuccess")
    val isSuccess: Boolean,
    val code: String,
    val message: String,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val result: T?
) {
    companion object {
        /**
         * 성공적인 응답을 생성합니다.
         *
         * @param result 반환할 실제 데이터.
         * @return [ApiResponse] 객체.
         */
        fun <T> onSuccess(result: T): ApiResponse<T> {
            return ApiResponse(true, SuccessStatus._OK.getReasonHttpStatus().code, SuccessStatus._OK.getReasonHttpStatus().message, result)
        }

        /**
         * 커스텀 코드와 함께 성공적인 응답을 생성합니다.
         *
         * @param code 응답 코드.
         * @param result 반환할 실제 데이터.
         * @return [ApiResponse] 객체.
         */
        fun <T> of(code: BaseCode, result: T): ApiResponse<T> {
            return ApiResponse(true, code.getReasonHttpStatus().code, code.getReasonHttpStatus().message, result)
        }

        /**
         * 실패 응답을 생성합니다.
         *
         * @param code 실패 코드.
         * @param message 실패 메시지.
         * @param data 실패 시 추가 정보 (선택 사항).
         * @return [ApiResponse] 객체.
         */
        fun <T> onFailure(code: String, message: String, data: T?): ApiResponse<T> {
            return ApiResponse(false, code, message, data)
        }
    }
}