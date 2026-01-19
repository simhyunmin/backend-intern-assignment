package com.simhyeonmin.assignment.global.apiPayload.exception.handler

import com.simhyeonmin.assignment.global.apiPayload.code.BaseErrorCode
import com.simhyeonmin.assignment.global.apiPayload.exception.GeneralException

/**
 * JWT 관련 예외를 처리하는 핸들러.
 */
class JwtHandler(errorCode: BaseErrorCode) : GeneralException(errorCode)
