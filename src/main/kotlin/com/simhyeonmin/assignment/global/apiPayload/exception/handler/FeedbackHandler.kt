package com.simhyeonmin.assignment.global.apiPayload.exception.handler

import com.simhyeonmin.assignment.global.apiPayload.code.BaseErrorCode
import com.simhyeonmin.assignment.global.apiPayload.exception.GeneralException

/**
 * 피드백(Feedback) 관련 비즈니스 로직에서 발생하는 예외를 처리하는 핸들러.
 */
class FeedbackHandler(errorCode: BaseErrorCode) : GeneralException(errorCode)
