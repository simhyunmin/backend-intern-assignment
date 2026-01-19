package com.simhyeonmin.assignment.domain.admin.service

import com.simhyeonmin.assignment.presentation.admin.dto.ReportDto
import com.simhyeonmin.assignment.domain.chat.ChatRepository
import com.simhyeonmin.assignment.domain.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * 관리자 전용 분석 및 보고 기능의 비즈니스 로직을 처리하는 서비스.
 */
@Service
@Transactional(readOnly = true)
class AdminService(
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) {

    /**
     * 최근 24시간 동안의 핵심 사용자 활동 지표를 집계합니다.
     * @return 활동 지표 DTO.
     */
    fun getActivityReport(): ReportDto.ActivityReportResponse {
        val since = LocalDateTime.now().minusDays(1)

        val signupCount = userRepository.countByCreatedAtAfter(since)
        val chatCreationCount = chatRepository.countByCreatedAtAfter(since)

        // 로그인 성공 이벤트는 별도로 로깅하지 않으므로, 시연의 편의성을 위해 '로그인 수'는 0으로 고정 응답합니다.
        // 실제 프로덕션 환경에서는 이벤트 소싱이나 별도 로그 테이블을 통해 집계하는 것이 이상적입니다.
        val loginCount = 0L

        return ReportDto.ActivityReportResponse(
            signupCount = signupCount,
            loginCount = loginCount,
            chatCreationCount = chatCreationCount
        )
    }

    /**
     * 최근 24시간 동안의 모든 대화 기록을 CSV 형식의 문자열로 생성합니다.
     * @return CSV 데이터가 포함된 문자열.
     */
    fun getChatReportAsCsv(): String {
        val since = LocalDateTime.now().minusDays(1)
        val chats = chatRepository.findAllWithUserByCreatedAtAfter(since)

        val header = """chatId","userId","userEmail","question","answer","createdAt"""
        val rows = chats.map {
            val (id, thread, question, answer) = it
            // CSV 표준에 따라 큰따옴표(")를 두 개("")로 이스케이프 처리합니다.
            val escapedQuestion = question.replace("\"", "\"\"")
            val escapedAnswer = answer?.replace("\"", "\"\"") ?: ""
            """${id}","${thread.user.id}","${thread.user.email}","$escapedQuestion","$escapedAnswer","${it.createdAt}"""
        }

        // 대용량 데이터가 아닌 경우, 외부 라이브러리 없이 직접 문자열을 조립하는 것이 가장 빠르고 의존성이 없는 방법입니다.
        return (listOf(header) + rows).joinToString("\n")
    }
}
