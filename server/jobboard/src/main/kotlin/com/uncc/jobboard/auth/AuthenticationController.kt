package com.uncc.jobboard.auth

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.jvm.internal.Intrinsics.Kotlin


@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(private val service: AuthenticationService) {

    @PostMapping("/register")
    fun register(
            @RequestBody request: RegisterRequest?
    ): ResponseEntity<AuthenticationResponse> {
        val response : AuthenticationResponse = service!!.register(request!!)
        logger.info(response.toString())
        return ResponseEntity.ok<AuthenticationResponse>(response)
    }

    @PostMapping("/authenticate")
    fun authenticate(
            @RequestBody request: AuthenticationRequest?
    ): ResponseEntity<AuthenticationResponse> {
        return ResponseEntity.ok<AuthenticationResponse>(service!!.authenticate(request!!))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthenticationController::class.java)
    }
}