package com.simhyeonmin.assignment.global.apiPayload.exception

import com.simhyeonmin.assignment.global.response.ApiResponse
import com.simhyeonmin.assignment.global.apiPayload.code.ErrorReasonDTO
import com.simhyeonmin.assignment.global.apiPayload.code.status.ErrorStatus
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.LinkedHashMap
import java.util.Optional

/**
 * 전역 예외 처리 핸들러.
 * 모든 REST 컨트롤러에서 발생하는 예외를 중앙 집중식으로 처리하여 일관된 응답을 제공합니다.
 */
@RestControllerAdvice
class ExceptionAdvice : ResponseEntityExceptionHandler() {

    /**
     * `@Valid` 어노테이션을 사용한 요청 본문(RequestBody) 유효성 검사 실패 시 발생하는 예외를 처리합니다.
     * 필드별 에러 메시지를 수집하여 반환합니다.
     *
     * @param e 발생한 [MethodArgumentNotValidException] 예외.
     * @param headers HTTP 헤더.
     * @param status HTTP 상태 코드.
     * @param request 현재 웹 요청.
     * @return [ResponseEntity] 형태의 에러 응답.
     */
    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? { // Return type matches super method
        val errors = LinkedHashMap<String, String>()

        e.bindingResult.fieldErrors.forEach { fieldError ->
            val fieldName = fieldError.field
            val errorMessage = Optional.ofNullable(fieldError.defaultMessage).orElse("")
            errors.merge(fieldName, errorMessage) { existingErrorMessage, newErrorMessage ->
                "$existingErrorMessage, $newErrorMessage"
            }
        }
        return handleExceptionInternalArgs(e, headers, ErrorStatus._BAD_REQUEST, request, errors)
    }

    /**
     * `@Validated` 어노테이션을 사용한 메서드 파라미터 유효성 검사 실패 시 발생하는 예외를 처리합니다.
     *
     * @param e 발생한 [ConstraintViolationException] 예외.
     * @param request 현재 웹 요청.
     * @return [ResponseEntity] 형태의 에러 응답.
     */
    @ExceptionHandler
    fun validation(e: ConstraintViolationException, request: WebRequest): ResponseEntity<Any> {
        return handleExceptionInternalConstraint(e, HttpHeaders.EMPTY, ErrorStatus._BAD_REQUEST, request)
    }

    /**
     * 모든 처리되지 않은 일반 예외를 처리합니다. (500 Internal Server Error)
     *
     * @param e 발생한 [Exception] 예외.
     * @param request 현재 웹 요청.
     * @return [ResponseEntity] 형태의 에ror 응답.
     */
    @ExceptionHandler
    fun exception(e: Exception, request: WebRequest): ResponseEntity<Any> {
        e.printStackTrace()
        return handleExceptionInternalFalse(e, HttpHeaders.EMPTY, ErrorStatus._INTERNAL_SERVER_ERROR, ErrorStatus._INTERNAL_SERVER_ERROR.getReasonHttpStatus().httpStatus, request, e.message)
    }

    /**
     * [GeneralException]을 상속받는 커스텀 비즈니스 예외를 처리합니다.
     *
     * @param generalException 발생한 [GeneralException] 예외.
     * @param request 현재 HTTP 서블릿 요청.
     * @return [ResponseEntity] 형태의 에러 응답.
     */
    @ExceptionHandler(value = [GeneralException::class])
    fun onThrowException(generalException: GeneralException, request: HttpServletRequest): ResponseEntity<Any> {
        val errorReasonHttpStatus = generalException.getErrorReasonHttpStatus()
        return handleExceptionInternal(generalException, errorReasonHttpStatus, null, request)
    }

    // --- 헬퍼 메서드 ---

    private fun handleExceptionInternal(
        e: Exception,
        reason: ErrorReasonDTO,
        headers: HttpHeaders?, // Can be nullable here, as per super method
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        val body = ApiResponse.onFailure(reason.code, reason.message, null)
        val webRequest = ServletWebRequest(request)
        return super.handleExceptionInternal(
            e,
            body,
            headers ?: HttpHeaders(),
            reason.httpStatus,
            webRequest
        ) ?: ResponseEntity.status(reason.httpStatus).headers(headers).body(body) as ResponseEntity<Any>
    }

    private fun handleExceptionInternalFalse(
        e: Exception,
        headers: HttpHeaders, // Non-nullable for internal consistency where HttpHeaders.EMPTY is passed
        errorCommonStatus: ErrorStatus,
        status: HttpStatus,
        request: WebRequest,
        errorPoint: String?
    ): ResponseEntity<Any> {
        val body = ApiResponse.onFailure(errorCommonStatus.getReasonHttpStatus().code, errorCommonStatus.getReasonHttpStatus().message, errorPoint)
        return super.handleExceptionInternal(
            e,
            body,
            headers,
            status,
            request
        ) ?: ResponseEntity.status(status).headers(headers).body(body) as ResponseEntity<Any>
    }

    private fun handleExceptionInternalArgs(
        e: Exception,
        headers: HttpHeaders, // Non-nullable for internal consistency where HttpHeaders.EMPTY is passed
        errorCommonStatus: ErrorStatus,
        request: WebRequest,
        errorArgs: Map<String, String>
    ): ResponseEntity<Any> {
        val body = ApiResponse.onFailure(errorCommonStatus.getReasonHttpStatus().code, errorCommonStatus.getReasonHttpStatus().message, errorArgs)
        return super.handleExceptionInternal(
            e,
            body,
            headers,
            errorCommonStatus.getReasonHttpStatus().httpStatus,
            request
        ) ?: ResponseEntity.status(errorCommonStatus.getReasonHttpStatus().httpStatus).headers(headers).body(body) as ResponseEntity<Any>
    }

    private fun handleExceptionInternalConstraint(
        e: Exception,
        headers: HttpHeaders, // Non-nullable for internal consistency where HttpHeaders.EMPTY is passed
        errorCommonStatus: ErrorStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        val body = ApiResponse.onFailure(errorCommonStatus.getReasonHttpStatus().code, errorCommonStatus.getReasonHttpStatus().message, null)
        return super.handleExceptionInternal(
            e,
            body,
            headers,
            errorCommonStatus.getReasonHttpStatus().httpStatus,
            request
        ) ?: ResponseEntity.status(errorCommonStatus.getReasonHttpStatus().httpStatus).headers(headers).body(body) as ResponseEntity<Any>
    }
}