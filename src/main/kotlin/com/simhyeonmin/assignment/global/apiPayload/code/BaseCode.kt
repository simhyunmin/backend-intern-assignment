package com.simhyeonmin.assignment.global.apiPayload.code

interface BaseCode {
    fun getReason(): ReasonDTO
    fun getReasonHttpStatus(): ReasonDTO
}
