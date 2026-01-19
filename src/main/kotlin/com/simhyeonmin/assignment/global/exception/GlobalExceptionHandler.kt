package com.simhyeonmin.assignment.global.exception

import com.simhyeonmin.assignment.global.response.ApiResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    // 미리 만들어두고 과제 요건에 맞춰 내용만 살짝 수정
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ApiResponse<Nothing> {
        return ApiResponse.error("400", e.message ?: "Invalid Input")
    }
    
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ApiResponse<Nothing> {
        return ApiResponse.error("500", "Internal Server Error")
    }
}
