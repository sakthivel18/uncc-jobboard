package com.uncc.jobboard.config

import com.uncc.jobboard.token.Token
import com.uncc.jobboard.token.TokenRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.Optional

@Component
class JwtAuthenticationFilter(
        private val jwtService: JwtService,
        private val userDetailsService: UserDetailsService,
        private val tokenRepository: TokenRepository
        ) : OncePerRequestFilter() {


    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
            @NonNull request: HttpServletRequest,
            @NonNull response: HttpServletResponse,
            @NonNull filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }
        val jwt: String = authHeader.substring(7)
        val userEmail: String? = jwtService!!.extractUsername(jwt)
        if (userEmail != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(userEmail)
            val token : Optional<Token?>? = tokenRepository.findByToken(jwt)
            var isTokenValid : Boolean = false
            if (token!!.isPresent) {
                isTokenValid = token.map { t -> !t?.expired!!  && !t.revoked!! }.orElse(false)
            }
            if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
                val authToken = UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.authorities
                )
                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authToken
            }
        }
        filterChain.doFilter(request, response)
    }
}