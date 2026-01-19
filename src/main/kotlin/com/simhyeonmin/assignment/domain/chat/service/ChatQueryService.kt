package com.simhyeonmin.assignment.domain.chat.service

import com.simhyeonmin.assignment.domain.chat.ChatRepository
import com.simhyeonmin.assignment.domain.chat.ThreadRepository
import com.simhyeonmin.assignment.domain.chat.Thread
import com.simhyeonmin.assignment.presentation.chat.dto.ChatResponseDTO
import com.simhyeonmin.assignment.domain.user.UserRepository
import com.simhyeonmin.assignment.global.apiPayload.code.status.ErrorStatus
import com.simhyeonmin.assignment.global.apiPayload.exception.handler.MemberHandler
import com.simhyeonmin.assignment.global.enums.UserRole
import com.simhyeonmin.assignment.global.jwt.JwtProvider
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 챗봇 대화의 'Query' (조회) 관련 비즈니스 로직을 처리하는 서비스.
 * CQS 원칙에 따라, 데이터를 조회만 하는 오퍼레이션들을 담당합니다.
 */
@Service
@Transactional(readOnly = true)
class ChatQueryService(
    private val threadRepository: ThreadRepository,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
) {
    /**
     * 사용자의 대화 기록을 조회합니다.
     * - 일반 사용자(MEMBER)는 자신의 대화 기록만 조회합니다.
     * - 관리자(ADMIN)는 모든 사용자의 대화 기록을 조회합니다.
     *
     * @param authorization JWT 인증 헤더.
     * @param page 페이지 번호.
     * @param size 페이지 크기.
     * @param sort 정렬 방향.
     * @return 대화 기록을 포함하는 DTO.
     */
    fun getChatHistories(authorization: String, page: Int, size: Int, sort: String): ChatResponseDTO.AllChatHistories {
        val userId = jwtProvider.getUserId(jwtProvider.resolveToken(authorization) ?: throw MemberHandler(ErrorStatus._UNAUTHORIZED))
        val user = userRepository.findById(userId).orElseThrow { MemberHandler(ErrorStatus.MEMBER_NOT_FOUND) }

        val sortDirection = if (sort.equals("ASC", ignoreCase = true)) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, "createdAt"))

        val threadsPage: Page<Thread> = if (user.role == UserRole.ADMIN) {
            // 관리자는 모든 스레드를 조회
            threadRepository.findAll(pageable)
        } else {
            // 일반 사용자는 자신의 스레드만 조회
            threadRepository.findAllByUser(user, pageable)
        }

        val chatHistories = threadsPage.content.map { thread ->
            // 각 스레드에 대해 모든 메시지를 조회합니다.
            val messages = chatRepository.findAllByThreadOrderByCreatedAtDesc(thread, Pageable.unpaged()).map { chat ->
                ChatResponseDTO.ChatMessage(
                    threadId = thread.id!!,
                    messageId = chat.id!!,
                    question = chat.question,
                    answer = chat.answer,
                    createdAt = chat.createdAt
                )
            }.reversed()

            ChatResponseDTO.ChatHistory(
                threadId = thread.id!!,
                messages = messages
            )
        }

        return ChatResponseDTO.AllChatHistories(threads = chatHistories)
    }
}
