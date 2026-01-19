package com.simhyeonmin.assignment.presentation.admin.dto

import io.swagger.v3.oas.annotations.media.Schema

class ReportDto {

    data class ActivityReportResponse(
        @Schema(description = "최근 24시간 내 신규 가입자 수")
        val signupCount: Long,
        @Schema(description = "최근 24시간 내 로그인 수")
        val loginCount: Long,
        @Schema(description = "최근 24시간 내 대화 생성 수")
        val chatCreationCount: Long
    )
}
