package com.simhyeonmin.assignment.presentation.chat

import com.simhyeonmin.assignment.presentation.chat.dto.ChatRequestDTO
import com.simhyeonmin.assignment.presentation.chat.dto.ChatResponseDTO
import com.simhyeonmin.assignment.domain.chat.service.ChatCommandService
import com.simhyeonmin.assignment.domain.chat.service.ChatQueryService
import com.simhyeonmin.assignment.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 챗봇 대화 관련 API 엔드포인트를 제공하는 컨트롤러.
 * Command(생성, 삭제)와 Query(조회) 로직을 각각의 서비스에 위임합니다.
 * 이 컨트롤러의 API들은 AI Agent가 사용자와 상호작용하거나 대화 기록을 분석하기 위한 'Tool'로 호출될 수 있습니다.
 */
@RestController
@RequestMapping("/chats")
@Tag(name = "Chat", description = "챗봇 대화 관련 API")
class ChatController(
    private val chatCommandService: ChatCommandService,
    private val chatQueryService: ChatQueryService
) {

    /**
     * 챗봇에게 질문을 전송하고 AI의 응답을 받습니다.
     * AI Agent가 사용자를 대신하여 질문하거나, 대화 흐름을 시작하기 위한 핵심 Tool API입니다.
     *
     * @param authorization JWT 인증 헤더.
     * @param request 챗봇 질문 요청 DTO ([ChatRequestDTO.Ask]).
     * @return 챗봇 응답 정보를 포함하는 [ApiResponse] 응답.
     */
    @PostMapping
    @Operation(summary = "챗봇 질문 및 응답 생성", description = "사용자 질문에 대한 챗봇의 응답을 생성합니다. AI Agent의 핵심 상호작용 Tool입니다.")
    fun ask(
        @RequestHeader(value = "Authorization") authorization: String,
        @RequestBody @Valid request: ChatRequestDTO.Ask
    ): ResponseEntity<ApiResponse<ChatResponseDTO.ChatMessage>> {
        val response = chatCommandService.ask(authorization, request)
        return ResponseEntity.ok(ApiResponse.onSuccess(response))
    }

    /**
     * 사용자의 전체 대화 기록을 조회합니다.
     * AI Agent가 사용자의 과거 대화 문맥(Context)을 파악하거나, 분석 리포트를 생성하기 위한 데이터 소스로 활용됩니다.
     *
     * @param authorization JWT 인증 헤더.
     * @return 모든 대화 기록을 포함하는 [ApiResponse] 응답.
     */
    @GetMapping
    @Operation(summary = "대화 기록 조회", description = "현재 사용자의 모든 대화 스레드 및 메시지 기록을 조회합니다. AI Agent의 문맥 분석 Tool로 사용됩니다.")
    fun getChatHistories(
        @RequestHeader(value = "Authorization") authorization: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "DESC") sort: String
    ): ResponseEntity<ApiResponse<ChatResponseDTO.AllChatHistories>> {
        val response = chatQueryService.getChatHistories(authorization, page, size, sort)
        return ResponseEntity.ok(ApiResponse.onSuccess(response))
    }

    /**
     * 특정 대화 스레드를 삭제합니다.
     * 사용자의 데이터 삭제 요청을 처리하거나, Agent가 특정 대화 주제를 잊도록(forget) 하는 데 사용될 수 있습니다.
     *
     * @param authorization JWT 인증 헤더.
     * @param threadId 삭제할 스레드의 ID.
     * @return 성공 여부를 나타내는 [ApiResponse] 응답.
     */
    @DeleteMapping("/{threadId}")
    @Operation(summary = "스레드 삭제", description = "특정 대화 스레드와 관련된 모든 대화 기록을 삭제합니다.")
    fun deleteThread(
        @RequestHeader(value = "Authorization") authorization: String,
        @PathVariable threadId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        chatCommandService.deleteThread(authorization, threadId)
        return ResponseEntity.ok(ApiResponse.onSuccess(Unit)) // 성공 시 body 없음
    }
}