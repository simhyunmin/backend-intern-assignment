package com.simhyeonmin.assignment.global.apiPayload.code.status

import com.simhyeonmin.assignment.global.apiPayload.code.BaseErrorCode
import com.simhyeonmin.assignment.global.apiPayload.code.ErrorReasonDTO
import org.springframework.http.HttpStatus

/**
 * 에러 응답에 대한 상태 코드를 정의하는 Enum.
 */
enum class ErrorStatus(
    private val httpStatus: HttpStatus,
    private val code: String,
    private val message: String
) : BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 멤버 관련
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "존재하지 않는 회원입니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "MEMBER4002", "이미 존재하는 회원입니다."),
    INVALID_MEMBER_INFO(HttpStatus.BAD_REQUEST, "MEMBER4003", "회원 필수 정보가 누락되었습니다."),

    // JWT 관련
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "JWT4001", "유효하지 않은 JWT 토큰입니다."),
    EXPIRED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "JWT4002", "만료된 JWT 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "JWT4003", "유효하지 않은 리프레시 토큰입니다. 재로그인이 필요합니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "JWT4004", "만료된 리프레시 토큰입니다. 재로그인이 필요합니다."),

    // 스레드 관련
    THREAD_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4009", "존재하지 않는 스레드입니다."),

    // 채팅 관련
    CHAT_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT4010", "존재하지 않는 대화입니다."),

    // 피드백 관련
    FEEDBACK_NOT_FOUND(HttpStatus.NOT_FOUND, "FEEDBACK4001", "존재하지 않는 피드백입니다."),
    FEEDBACK_ALREADY_EXISTS(HttpStatus.CONFLICT, "FEEDBACK4002", "이미 해당 대화에 대한 피드백이 존재합니다.");


    override fun getReason(): ErrorReasonDTO {
        return ErrorReasonDTO(
            httpStatus = httpStatus,
            isSuccess = false,
            code = code,
            message = message
        )
    }

    override fun getReasonHttpStatus(): ErrorReasonDTO {
        return ErrorReasonDTO(
            httpStatus = httpStatus,
            isSuccess = false,
            code = code,
            message = message
        )
    }
}