package com.simhyeonmin.assignment.infrastructure.ai

import com.simhyeonmin.assignment.core.ai.LlmClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * LlmClient 인터페이스의 Mock 구현체.
 * 실제 LLM 서비스 호출 없이 테스트 및 개발 환경에서 사용될 수 있도록 미리 정의된 응답을 반환합니다.
 */
@Service
class MockLlmClient : LlmClient {

    private val log = LoggerFactory.getLogger(this::class.java)

    /**
     * 사용자 질문에 대한 Mock 응답을 생성합니다.
     *
     * @param systemMessage 현재 Mock 구현에서는 사용되지 않지만, 향후 RAG 도입 시 검색된 문서가 주입될 확장 파라미터입니다.
     * @param userMessage 사용자 질문.
     * @return 미리 정의된 고정 응답 문자열.
     */
    override fun chat(systemMessage: String, userMessage: String): String {
        // systemMessage의 주입 여부를 로그로 확인하여, RAG 파이프라인의 데이터 흐름을 검증할 수 있습니다.
        if (systemMessage.isNotBlank()) {
            log.info("[MockLlmClient] System Message (RAG Context) was injected. Length: ${systemMessage.length}")
        }

        return "안녕하세요! 저는 Sionic AI의 챗봇입니다. '$userMessage'에 대해 답변해 드릴게요."
    }
}
