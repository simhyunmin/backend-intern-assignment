package com.simhyeonmin.assignment.presentation.chat.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 채팅 응답 데이터 전송 객체 (DTO)들을 포함하는 클래스.
 */
class ChatResponseDTO {

    /**
     * 챗봇 응답을 위한 DTO.
     *
     * @property threadId 이 응답이 속한 스레드 ID.
     * @property messageId 이 응답 메시지의 ID.
     * @property content 챗봇의 응답 내용.
     * @property createdAt 응답 생성 시간.
     */
    data class ChatMessage(
        @Schema(description = "스레드 ID", example = "1")
        val threadId: Long,
        @Schema(description = "메시지 ID", example = "1")
        val messageId: Long,
        @Schema(description = "사용자 질문", example = "오늘 날씨 어때?")
        val question: String,
        @Schema(description = "챗봇 답변", example = "오늘은 맑고 화창한 날씨입니다.")
        val answer: String?,
        @Schema(description = "응답 생성 시간", example = "2026-01-01T10:30:00")
        val createdAt: LocalDateTime
    )

    /**
     * 대화 목록 조회를 위한 DTO.
     *
     * @property threadId 스레드 ID.
     * @property messages 해당 스레드에 속한 대화 메시지 목록.
     */
    data class ChatHistory(
        @Schema(description = "스레드 ID", example = "1")
        val threadId: Long,
        @Schema(description = "대화 메시지 목록")
        val messages: List<ChatMessage>
    )

    /**
     * 대화 목록 전체 조회를 위한 DTO.
     *
     * @property threads 대화 스레드 목록.
     */
    data class AllChatHistories(
        @Schema(description = "대화 스레드 목록")
        val threads: List<ChatHistory>
    )
}
