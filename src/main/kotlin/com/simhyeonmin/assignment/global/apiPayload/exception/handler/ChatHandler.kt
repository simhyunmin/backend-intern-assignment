package com.simhyeonmin.assignment.global.apiPayload.exception.handler

import com.simhyeonmin.assignment.global.apiPayload.code.BaseErrorCode
import com.simhyeonmin.assignment.global.apiPayload.exception.GeneralException

/**
 * 채팅(Chat) 관련 비즈니스 로직에서 발생하는 예외를 처리하는 핸들러.
 */
class ChatHandler(errorCode: BaseErrorCode) : GeneralException(errorCode)
