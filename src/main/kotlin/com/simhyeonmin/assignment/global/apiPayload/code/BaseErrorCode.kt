package com.simhyeonmin.assignment.global.apiPayload.code

interface BaseErrorCode {
    fun getReason(): ErrorReasonDTO
    fun getReasonHttpStatus(): ErrorReasonDTO
}
