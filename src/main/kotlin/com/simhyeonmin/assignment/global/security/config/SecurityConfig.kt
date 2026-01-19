package com.simhyeonmin.assignment.global.security.config

import com.simhyeonmin.assignment.global.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.util.*

/**
 * Spring Security 설정을 위한 Configuration 클래스.
 * JWT 기반 인증 및 CORS 정책을 설정합니다.
 *
 * @property jwtAuthenticationFilter JWT 인증 필터.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    /**
     * 비밀번호 인코딩을 위한 [PasswordEncoder]를 빈으로 등록합니다.
     * BCrypt 해싱 알고리즘을 사용합니다.
     *
     * @return [BCryptPasswordEncoder] 인스턴스.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * HTTP 보안 필터 체인을 구성합니다.
     * CORS, CSRF 비활성화, 세션 관리 정책, 요청 권한 설정 및 JWT 필터 추가를 정의합니다.
     *
     * @param http [HttpSecurity] 객체.
     * @return 구성된 [SecurityFilterChain].
     * @throws Exception 보안 설정 중 발생할 수 있는 예외.
     */
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .cors { cors -> cors.configurationSource(corsConfigurationSource()) } // CORS 설정
            .csrf { csrf -> csrf.disable() } // CSRF 보호 비활성화
            .httpBasic { httpBasic -> httpBasic.disable() } // HTTP 기본 인증 비활성화
            .formLogin { formLogin -> formLogin.disable() } // 폼 로그인 비활성화
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // 세션 사용 안 함 (STATELESS)
            .authorizeHttpRequests { auth ->
                auth
                    // 화이트리스트 경로 설정: 이 경로들은 인증 없이 접근 가능
                    .requestMatchers(
                        "/auth/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/api-docs/**", // Add this
                        "/members/signup", // 회원가입
                        "/members/login",  // 로그인
                        "/refresh"         // 토큰 재발급
                    ).permitAll()
                    // ADMIN 경로는 ADMIN 역할 필요
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    // 나머지 모든 요청은 인증 필요
                    .anyRequest().authenticated()
            }
            // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 위한 [CorsConfigurationSource]를 빈으로 등록합니다.
     * 개발 및 테스트 환경을 위해 모든 오리진, 메서드, 헤더를 허용합니다. (실제 운영 환경에서는 제한 필요)
     *
     * @return [CorsConfigurationSource] 인스턴스.
     */
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("*") // 모든 오리진 허용
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // 모든 HTTP 메서드 허용
        config.allowedHeaders = listOf("*") // 모든 헤더 허용
        config.allowCredentials = false // 자격 증명 허용 안 함
        config.exposedHeaders = listOf("Authorization") // 클라이언트에서 'Authorization' 헤더에 접근 허용

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config) // 모든 경로에 대해 CORS 설정 적용
        return source
    }
}
