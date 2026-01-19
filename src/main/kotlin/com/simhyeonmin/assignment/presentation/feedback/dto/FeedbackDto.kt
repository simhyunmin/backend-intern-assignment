package com.simhyeonmin.assignment.presentation.feedback.dto

import com.simhyeonmin.assignment.global.enums.FeedbackStatus
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

class FeedbackDto {
    data class CreateRequest(
        @Schema(description = "피드백 긍정/부정 여부", example = "true")
        val positive: Boolean
    )

    data class UpdateStatusRequest(
        @Schema(description = "변경할 피드백 상태", example = "RESOLVED")
        val status: FeedbackStatus
    )

    data class FeedbackResponse(
        @Schema(description = "피드백 ID")
        val feedbackId: Long,
        @Schema(description = "피드백을 작성한 사용자 ID")
        val userId: Long,
        @Schema(description = "피드백 대상 대화 ID")
        val chatId: Long,
        @Schema(description = "긍정/부정 여부")
        val positive: Boolean,
        @Schema(description = "피드백 상태")
        val status: FeedbackStatus,
        @Schema(description = "생성 일시")
        val createdAt: LocalDateTime
    )
}
