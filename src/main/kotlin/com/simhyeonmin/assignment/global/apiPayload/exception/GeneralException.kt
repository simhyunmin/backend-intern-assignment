package com.simhyeonmin.assignment.global.apiPayload.exception

import com.simhyeonmin.assignment.global.apiPayload.code.BaseErrorCode
import com.simhyeonmin.assignment.global.apiPayload.code.ErrorReasonDTO

/**
 * 모든 비즈니스 예외의 기본이 되는 추상 클래스.
 *
 * @property code 예외를 나타내는 [BaseErrorCode].
 */
abstract class GeneralException(
    private val code: BaseErrorCode
) : RuntimeException(code.getReason().message) {

    /**
     * 예외의 에러 상세 정보를 반환합니다.
     *
     * @return [ErrorReasonDTO] 객체.
     */
    fun getErrorReason(): ErrorReasonDTO {
        return this.code.getReason()
    }

    /**
     * 예외의 HTTP 상태 코드를 포함한 에러 상세 정보를 반환합니다.
     *
     * @return [ErrorReasonDTO] 객체.
     */
    fun getErrorReasonHttpStatus(): ErrorReasonDTO {
        return this.code.getReasonHttpStatus()
    }
}
