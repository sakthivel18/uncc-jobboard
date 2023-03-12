package com.uncc.jobboard.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

@Configuration
@EnableWebSecurity
class SecurityConfiguration(
        private val jwtAuthFilter: JwtAuthenticationFilter,
        private val authenticationProvider: AuthenticationProvider,
        private val logoutHandler: LogoutHandler
) {

//    @Bean
//    fun webSecurityCustomizer(): WebSecurityCustomizer {
//        return WebSecurityCustomizer { web: WebSecurity ->
//            web.ignoring() // Spring Security should completely ignore URLs starting with /resources/
//                    .requestMatchers("/api/v1/auth/**")
//        }
//    }

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessHandler(LogoutSuccessHandler { request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication? -> SecurityContextHolder.clearContext() })
        return http.build()
    }
}