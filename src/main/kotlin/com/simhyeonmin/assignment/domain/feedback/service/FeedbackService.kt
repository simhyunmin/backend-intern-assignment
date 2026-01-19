package com.simhyeonmin.assignment.domain.feedback.service

import com.simhyeonmin.assignment.domain.chat.ChatRepository
import com.simhyeonmin.assignment.domain.feedback.Feedback
import com.simhyeonmin.assignment.domain.feedback.FeedbackRepository
import com.simhyeonmin.assignment.domain.feedback.dto.FeedbackDto
import com.simhyeonmin.assignment.domain.user.UserRepository
import com.simhyeonmin.assignment.global.apiPayload.code.status.ErrorStatus
import com.simhyeonmin.assignment.global.apiPayload.exception.handler.ChatHandler
import com.simhyeonmin.assignment.global.apiPayload.exception.handler.FeedbackHandler
import com.simhyeonmin.assignment.global.apiPayload.exception.handler.MemberHandler
import com.simhyeonmin.assignment.global.enums.UserRole
import com.simhyeonmin.assignment.global.jwt.JwtProvider
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 피드백 데이터 처리를 위한 비즈니스 로직을 담당하는 서비스.
 */
@Service
@Transactional(readOnly = true)
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val jwtProvider: JwtProvider
) {

    /**
     * 특정 대화에 대한 신규 피드백을 생성합니다.
     *
     * @param authorization JWT 인증 헤더.
     * @param chatId 피드백을 남길 대화의 ID.
     * @param request 긍정/부정 피드백 요청 DTO.
     * @return 생성된 Feedback 엔티티.
     * @throws MemberHandler 사용자를 찾을 수 없거나 권한이 없는 경우.
     * @throws ChatHandler 대화를 찾을 수 없는 경우.
     * @throws FeedbackHandler 이미 피드백이 존재하는 경우.
     */
    @Transactional
    fun createFeedback(authorization: String, chatId: Long, request: FeedbackDto.CreateRequest): Feedback {
        val token = jwtProvider.resolveToken(authorization) ?: throw MemberHandler(ErrorStatus._UNAUTHORIZED)
        val userId = jwtProvider.getUserId(token)
        val user = userRepository.findById(userId).orElseThrow { MemberHandler(ErrorStatus.MEMBER_NOT_FOUND) }

        val chat = chatRepository.findById(chatId).orElseThrow { ChatHandler(ErrorStatus.CHAT_NOT_FOUND) }

        // 서비스 레벨에서 중복 생성을 방지하여 데이터 정합성을 보장합니다.
        // DB unique constraint는 최후의 방어선 역할을 합니다.
        if (feedbackRepository.existsByUserAndChat(user, chat)) {
            throw FeedbackHandler(ErrorStatus.FEEDBACK_ALREADY_EXISTS)
        }

        // 비즈니스 규칙: 피드백은 대화의 소유자 또는 관리자만 생성할 수 있습니다.
        if (user.role != UserRole.ADMIN && chat.thread.user.id != user.id) {
            throw MemberHandler(ErrorStatus._FORBIDDEN)
        }

        val feedback = Feedback(
            user = user,
            chat = chat,
            positive = request.positive
        )

        return feedbackRepository.save(feedback)
    }

    /**
     * 조건에 맞는 피드백 목록을 조회합니다.
     * 관리자는 모든 피드백을, 일반 사용자는 자신의 피드백만 조회할 수 있습니다.
     *
     * @param authorization JWT 인증 헤더.
     * @param positive 긍정/부정 여부 필터. null일 경우 필터링하지 않습니다.
     * @return 조건에 맞는 Page<Feedback> 객체.
     */
    fun getFeedbacks(authorization: String, page: Int, size: Int, sort: String, positive: Boolean?): Page<Feedback> {
        val token = jwtProvider.resolveToken(authorization) ?: throw MemberHandler(ErrorStatus._UNAUTHORIZED)
        val userId = jwtProvider.getUserId(token)
        val user = userRepository.findById(userId).orElseThrow { MemberHandler(ErrorStatus.MEMBER_NOT_FOUND) }

        val sortDirection = if (sort.equals("ASC", ignoreCase = true)) Sort.Direction.ASC else Sort.Direction.DESC
        val pageable = PageRequest.of(page, size, Sort.by(sortDirection, "createdAt"))

        // JPA Specification을 사용하여 'positive' 필터와 사용자 권한에 따른 동적 쿼리를 생성합니다.
        // 이는 여러 조건 조합에 대해 메서드를 중복 생성하는 것을 방지하는 확장성 있는 설계입니다.
        val spec = Specification.where<Feedback> { root, _, builder ->
            val userPredicate = if (user.role != UserRole.ADMIN) {
                builder.equal(root.get<Any>("user"), user)
            } else {
                null
            }

            val positivePredicate = positive?.let {
                builder.equal(root.get<Boolean>("positive"), it)
            }

            builder.and(*(listOfNotNull(userPredicate, positivePredicate).toTypedArray()))
        }

        return feedbackRepository.findAll(spec, pageable)
    }

    /**
     * 피드백의 처리 상태를 업데이트합니다. (관리자 전용 기능)
     *
     * @param feedbackId 상태를 변경할 피드백의 ID.
     * @param request 변경할 상태 정보.
     * @return 상태가 업데이트된 Feedback 엔티티.
     */
    @Transactional
    fun updateFeedbackStatus(feedbackId: Long, request: FeedbackDto.UpdateStatusRequest): Feedback {
        val feedback = feedbackRepository.findById(feedbackId).orElseThrow { FeedbackHandler(ErrorStatus.FEEDBACK_NOT_FOUND) }
        feedback.status = request.status
        return feedbackRepository.save(feedback)
    }
}
