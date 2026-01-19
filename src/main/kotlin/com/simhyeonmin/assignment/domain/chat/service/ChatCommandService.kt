package com.simhyeonmin.assignment.domain.chat.service

import com.simhyeonmin.assignment.domain.chat.Chat
import com.simhyeonmin.assignment.domain.chat.ChatRepository
import com.simhyeonmin.assignment.domain.chat.Thread
import com.simhyeonmin.assignment.domain.chat.ThreadRepository
import com.simhyeonmin.assignment.presentation.chat.dto.ChatRequestDTO
import com.simhyeonmin.assignment.presentation.chat.dto.ChatResponseDTO
import com.simhyeonmin.assignment.domain.user.UserRepository
import com.simhyeonmin.assignment.core.ai.LlmClient
import com.simhyeonmin.assignment.global.apiPayload.code.status.ErrorStatus
import com.simhyeonmin.assignment.global.apiPayload.exception.handler.MemberHandler
import com.simhyeonmin.assignment.global.jwt.JwtProvider
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.Duration
import org.slf4j.LoggerFactory

/**
 * 챗봇 대화의 'Command' (상태 변경) 관련 비즈니스 로직을 처리하는 서비스.
 * CQS 원칙에 따라, 데이터 생성, 수정, 삭제를 담당하며, AI Agent와의 상호작용을 위한 핵심 로직을 포함합니다.
 */
@Service
@Transactional
class ChatCommandService(
    private val threadRepository: ThreadRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
    private val llmClient: LlmClient
) {
    private val log = LoggerFactory.getLogger(this.javaClass)!!

    companion object {
        const val THREAD_INACTIVITY_THRESHOLD_MINUTES = 30L
    }

    /**
     * 사용자 질문에 대한 AI 응답을 생성하고 대화 기록을 저장합니다.
     * 이 메서드는 AI Agent가 사용자와의 대화를 수행하기 위한 핵심 진입점(Entrypoint) 역할을 합니다.
     *
     * @param authorization 인증을 위한 JWT 토큰.
     * @param request 사용자의 질문 및 추가 옵션(모델, 스트리밍 여부)을 담은 DTO.
     * @return 생성된 AI 응답 및 대화 정보를 담은 DTO.
     */
    fun ask(authorization: String, request: ChatRequestDTO.Ask): ChatResponseDTO.ChatMessage {
        val userId = jwtProvider.getUserId(jwtProvider.resolveToken(authorization) ?: throw MemberHandler(ErrorStatus._UNAUTHORIZED))
        val user = userRepository.findById(userId).orElseThrow { MemberHandler(ErrorStatus.MEMBER_NOT_FOUND) }

        // [Business Rule] 마지막 대화 후 30분이 경과했는지 확인하여 스레드 생명주기(신규/유지)를 결정합니다.
        val latestThread = threadRepository.findTopByUserOrderByLastActiveAtDesc(user).orElse(null)
        val currentThread = if (latestThread == null || Duration.between(latestThread.lastActiveAt, LocalDateTime.now()).toMinutes() >= THREAD_INACTIVITY_THRESHOLD_MINUTES) {
            threadRepository.save(Thread(user = user))
        } else {
            latestThread.apply { updateLastActiveAt() }
        }

        // 사용자 질문을 먼저 저장하여 모든 요청 기록을 보존합니다.
        val newChat = chatRepository.save(Chat(
            thread = currentThread,
            question = request.question,
            answer = null // AI 응답은 비동기적으로 또는 후속 처리로 업데이트될 수 있습니다.
        ))

        // [Future Enhancement] 스트리밍 및 동적 모델 선택 로직이 이 지점에 통합됩니다.
        log.info("Chat Request Options: isStreaming={}, model={}", request.isStreaming, request.model)

        // [RAG Ready] 향후 Vector DB 검색 또는 외부 API 호출을 통해 얻은 컨텍스트가
        // 이 지점에서 llmClient.chat()의 systemMessage 파라미터로 주입됩니다.
        val systemMessage = "" // 현재는 RAG 컨텍스트가 없으므로 비워둡니다.
        val userMessage = request.question
        val llmResponseContent = llmClient.chat(systemMessage, userMessage)

        // 생성된 답변으로 대화 기록을 업데이트합니다.
        newChat.answer = llmResponseContent
        val savedChat = chatRepository.save(newChat)

        return ChatResponseDTO.ChatMessage(
            threadId = savedChat.thread.id!!,
            messageId = savedChat.id!!,
            question = savedChat.question,
            answer = savedChat.answer,
            createdAt = savedChat.createdAt
        )
    }

    /**
     * 특정 대화 스레드와 관련된 모든 대화 기록을 삭제합니다.
     * 데이터 정합성을 위해 스레드와 하위 채팅 기록을 모두 트랜잭션 내에서 삭제합니다.
     *
     * @param authorization 인증을 위한 JWT 토큰.
     * @param threadId 삭제할 스레드의 ID.
     */
    fun deleteThread(authorization: String, threadId: Long) {
        val userId = jwtProvider.getUserId(jwtProvider.resolveToken(authorization) ?: throw MemberHandler(ErrorStatus._UNAUTHORIZED))
        val user = userRepository.findById(userId).orElseThrow { MemberHandler(ErrorStatus.MEMBER_NOT_FOUND) }

        val thread = threadRepository.findById(threadId).orElseThrow { MemberHandler(ErrorStatus.THREAD_NOT_FOUND) }
        
        // [Business Rule] 사용자는 자신이 생성한 스레드만 삭제할 수 있습니다.
        if (thread.user != user) {
            throw MemberHandler(ErrorStatus._FORBIDDEN)
        }

        chatRepository.deleteAllByThread(thread)
        threadRepository.delete(thread)
    }
}
