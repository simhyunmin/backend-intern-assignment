package com.simhyeonmin.assignment.global.config

import com.simhyeonmin.assignment.core.ai.LlmClient
import com.simhyeonmin.assignment.infrastructure.ai.MockLlmClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * LLM 클라이언트 구성을 위한 Configuration 클래스.
 * 현재 MockLlmClient를 기본 LlmClient 구현체로 제공합니다.
 * 실제 LLM 클라이언트를 사용하려면 이 구성을 변경해야 합니다.
 */
@Configuration
class LlmConfig {
    /**
     * MockLlmClient를 LlmClient 인터페이스의 구현체로 등록합니다.
     *
     * @return MockLlmClient 인스턴스.
     */
    @Bean
    fun llmClient(): LlmClient {
        return MockLlmClient()
    }
}
