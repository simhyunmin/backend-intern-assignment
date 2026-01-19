package com.simhyeonmin.assignment.global.apiPayload.code.status

import com.simhyeonmin.assignment.global.apiPayload.code.BaseCode
import com.simhyeonmin.assignment.global.apiPayload.code.ReasonDTO
import org.springframework.http.HttpStatus

/**
 * 성공 응답에 대한 상태 코드를 정의하는 Enum.
 */
enum class SuccessStatus(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val message: String
) : BaseCode {
    /**
     * 일반적인 성공 응답.
     */
    _OK(HttpStatus.OK, "COMMON200", "성공입니다.");

    override fun getReason(): ReasonDTO {
        return ReasonDTO(
            httpStatus = httpStatus,
            isSuccess = true,
            code = code,
            message = message
        )
    }

    override fun getReasonHttpStatus(): ReasonDTO {
        return ReasonDTO(
            httpStatus = httpStatus,
            isSuccess = true,
            code = code,
            message = message
        )
    }
}
