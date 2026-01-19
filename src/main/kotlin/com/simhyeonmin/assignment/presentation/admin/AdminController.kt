package com.simhyeonmin.assignment.presentation.admin

import com.simhyeonmin.assignment.domain.admin.service.AdminService
import com.simhyeonmin.assignment.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자용 API")
@SecurityRequirement(name = "bearerAuth")
class AdminController(
    private val adminService: AdminService
) {
    @GetMapping("/reports/activity")
    @Operation(summary = "사용자 활동 리포트 조회 API", description = "지난 24시간 동안의 사용자 활동(가입, 로그인, 채팅 생성)을 요약하여 제공합니다.")
    fun getActivityReport(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String
    ) = ApiResponse.Companion.onSuccess(adminService.getActivityReport())


    @GetMapping("/reports/chats/csv")
    @Operation(summary = "채팅 기록 CSV 다운로드 API", description = "지난 24시간 동안의 모든 채팅 기록을 CSV 파일 형태로 제공합니다.")
    fun getChatReportAsCsv(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authorization: String
    ): ResponseEntity<ByteArray> {
        val csvData = adminService.getChatReportAsCsv()
        val headers = HttpHeaders()
        headers.contentDisposition = ContentDisposition.builder("attachment")
            .filename("chat_report.csv", StandardCharsets.UTF_8)
            .build()
        headers.contentType = MediaType.TEXT_PLAIN

        // HTTP 응답 헤더에 Content-Disposition을 설정하여 브라우저가 파일 다운로드로 처리하도록 유도합니다.
        // UTF-8로 인코딩된 파일명을 사용하도록 설정합니다.
        return ResponseEntity.ok()
            .headers(headers)
            .body(csvData.toByteArray(StandardCharsets.UTF_8))
    }
}