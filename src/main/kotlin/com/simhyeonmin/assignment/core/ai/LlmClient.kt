package com.simhyeonmin.assignment.core.ai

/**
 * Large Language Model (LLM) 클라이언트와의 상호작용을 위한 인터페이스.
 * 외부 LLM 서비스와의 의존성을 추상화하여, 실제 구현체를 쉽게 교체할 수 있도록 합니다.
 */
interface LlmClient {
    /**
     * LLM에 채팅 응답을 요청합니다.
     *
     * @param systemMessage LLM의 역할을 정의하거나 RAG Context를 주입하기 위한 파라미터
     * @param userMessage 사용자로부터 입력받은 실제 질문.
     * @return LLM으로부터 받은 텍스트 응답.
     */
    fun chat(systemMessage: String, userMessage: String): String
}