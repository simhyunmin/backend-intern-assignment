package com.simhyeonmin.assignment.global.security

import com.simhyeonmin.assignment.global.jwt.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import com.simhyeonmin.assignment.global.security.CustomUserDetailsService

/**
 * JWT 기반 인증을 위한 필터.
 * 모든 요청에 대해 JWT 토큰을 검증하고, 유효한 경우 Spring Security 컨텍스트를 설정합니다.
 * 특정 경로(화이트리스트)에 대해서는 필터링을 건너뜕니다.
 *
 * @property jwtProvider JWT 토큰 생성 및 검증을 담당하는 유틸리티.
 * @property customUserDetailsService 사용자 정보를 로드하는 서비스.
 */
@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    // JWT 인증을 건너뛸 경로 목록 (화이트리스트)
    private val WHITE_LIST = listOf(
        "/auth/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/api-docs/**", // Add this
        "/members/signup", // 회원가입
        "/members/login",  // 로그인
        "/refresh"         // 토큰 재발급
    )

    private val antPathMatcher = AntPathMatcher()

    /**
     * 모든 HTTP 요청에 대해 JWT 토큰을 검증하고, 유효한 경우 SecurityContext에 인증 정보를 설정합니다.
     *
     * @param request HTTP 요청 객체.
     * @param response HTTP 응답 객체.
     * @param filterChain 필터 체인.
     * @throws ServletException 서블릿 예외 발생 시.
     * @throws IOException 입출력 예외 발생 시.
     */
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authHeader = request.getHeader("Authorization")
        println("====================================================")
        println("JWT FILTER CHECK")
        println("Request URI: ${request.requestURI}")
        println("Authorization Header: $authHeader")
        println("====================================================")
        val token = jwtProvider.resolveToken(authHeader)

        if (token != null && jwtProvider.validateToken(token)) {
            val userId = jwtProvider.getUserId(token)
            val userDetails = customUserDetailsService.loadUserByUsername(userId.toString())
            
            val authentication = UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.authorities
            )
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    /**
     * 현재 요청이 JWT 필터링을 건너뛰어야 하는지 여부를 결정합니다.
     * 화이트리스트에 있는 경로는 필터링되지 않습니다.
     *
     * @param request HTTP 요청 객체.
     * @return 필터링을 건너뛰어야 하면 true, 그렇지 않으면 false.
     * @throws ServletException 서블릿 예외 발생 시.
     */
    @Throws(ServletException::class)
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // AntPathMatcher를 사용하여 요청 URI가 화이트리스트 경로 중 하나와 일치하는지 확인
        return WHITE_LIST.any { whitePath -> antPathMatcher.match(whitePath, request.requestURI) }
    }
}
