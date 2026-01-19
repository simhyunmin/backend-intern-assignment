package com.simhyeonmin.assignment.presentation.feedback

import com.simhyeonmin.assignment.domain.feedback.Feedback
import com.simhyeonmin.assignment.presentation.feedback.dto.FeedbackDto
import com.simhyeonmin.assignment.domain.feedback.service.FeedbackService
import com.simhyeonmin.assignment.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 대화 품질에 대한 사용자 피드백을 관리하는 API 컨트롤러.
 * 수집된 피드백은 향후 AI 모델의 성능 개선 및 파인튜닝을 위한 데이터셋으로 활용될 수 있습니다.
 */
@RestController
@Tag(name = "Feedback", description = "피드백 관련 API")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    /**
     * 특정 대화에 대한 사용자 피드백을 생성합니다.
     * 이 API는 모델 응답의 품질을 평가하고, 사용자의 선호를 학습하기 위한 명시적인 시그널을 수집하는 역할을 합니다.
     *
     * @param authorization JWT 인증 헤더.
     * @param chatId 피드백을 남길 대화의 ID.
     * @param request 긍정/부정 피드백 요청 DTO.
     * @return 생성된 피드백의 상세 정보를 포함하는 응답.
     */
    @PostMapping("/chats/{chatId}/feedback")
    @Operation(summary = "특정 대화에 피드백 생성", description = "사용자는 자신의 대화에만, 관리자는 모든 대화에 피드백을 생성할 수 있습니다.")
    fun createFeedback(
        @RequestHeader(value = "Authorization") authorization: String,
        @PathVariable chatId: Long,
        @RequestBody @Valid request: FeedbackDto.CreateRequest
    ): ResponseEntity<ApiResponse<FeedbackDto.FeedbackResponse>> {
        val feedback = feedbackService.createFeedback(authorization, chatId, request)
        return ResponseEntity.ok(ApiResponse.Companion.onSuccess(feedback.toResponse()))
    }

    /**
     * 피드백 목록을 조회합니다.
     * AI Agent 또는 데이터 분석가가 모델의 응답 경향이나 사용자의 만족도를 분석하기 위한 데이터 소스로 사용됩니다.
     *
     * @param authorization JWT 인증 헤더.
     * @param positive 긍정/부정 여부로 필터링하기 위한 옵셔널 파라미터.
     * @return 피드백 목록의 페이지네이션된 응답.
     */
    @GetMapping("/feedbacks")
    @Operation(summary = "피드백 목록 조회", description = "사용자는 자신의 피드백만, 관리자는 모든 피드백을 조회할 수 있습니다. `positive` 파라미터로 필터링 가능합니다.")
    fun getFeedbacks(
        @RequestHeader(value = "Authorization") authorization: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "DESC") sort: String,
        @RequestParam(required = false) positive: Boolean?
    ): ResponseEntity<ApiResponse<Page<FeedbackDto.FeedbackResponse>>> {
        val feedbackPage = feedbackService.getFeedbacks(authorization, page, size, sort, positive)
        return ResponseEntity.ok(ApiResponse.Companion.onSuccess(feedbackPage.map { it.toResponse() }))
    }

    /**
     * 피드백의 처리 상태를 변경합니다. (관리자 전용)
     * 운영팀 또는 관리자가 특정 피드백에 대한 조치(예: 데이터셋 편입, 이상 사례 분석)를 완료했음을 표시하는 데 사용됩니다.
     *
     * @param authorization JWT 인증 헤더.
     * @param feedbackId 상태를 변경할 피드백의 ID.
     * @param request 변경할 상태(`RESOLVED`)를 담은 요청 DTO.
     * @return 상태가 변경된 피드백의 상세 정보.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/feedbacks/{feedbackId}/status")
    @Operation(summary = "피드백 상태 변경 (관리자 전용)", description = "관리자만 피드백의 상태를 변경할 수 있습니다.")
    fun updateFeedbackStatus(
        @RequestHeader(value = "Authorization") authorization: String,
        @PathVariable feedbackId: Long,
        @RequestBody @Valid request: FeedbackDto.UpdateStatusRequest
    ): ResponseEntity<ApiResponse<FeedbackDto.FeedbackResponse>> {
        val feedback = feedbackService.updateFeedbackStatus(feedbackId, request)
        return ResponseEntity.ok(ApiResponse.Companion.onSuccess(feedback.toResponse()))
    }

    // Feedback 엔티티를 FeedbackResponse DTO로 변환하는 확장 함수
    private fun Feedback.toResponse() = FeedbackDto.FeedbackResponse(
        feedbackId = this.id!!,
        userId = this.user.id!!,
        chatId = this.chat.id!!,
        positive = this.positive,
        status = this.status,
        createdAt = this.createdAt
    )
}