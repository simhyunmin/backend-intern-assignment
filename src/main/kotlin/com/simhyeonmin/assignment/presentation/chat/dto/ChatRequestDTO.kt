package com.simhyeonmin.assignment.presentation.chat.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

/**
 * 채팅 요청 데이터 전송 객체 (DTO)들을 포함하는 클래스.
 */
class ChatRequestDTO {

    /**
     * 챗봇에게 질문을 보내기 위한 요청 DTO.
     *
     * @property question 사용자 질문 내용. 비어 있을 수 없습니다.
     * @property isStreaming 응답을 스트리밍 방식으로 받을지 여부 (기본값: false).
     * @property model 사용할 LLM 모델 이름 (선택 사항).
     */
    data class Ask(
        @field:NotBlank(message = "질문 내용은 필수입니다.")
        @Schema(description = "사용자 질문 내용", example = "오늘 날씨 어때?")
        val question: String,

        @Schema(description = "응답을 스트리밍 방식으로 받을지 여부", example = "false")
        val isStreaming: Boolean = false,

        @Schema(description = "사용할 LLM 모델 이름 (선택 사항)", example = "gpt-4o-mini")
        val model: String? = null
    )
}
