package com.uncc.jobboard.token

import com.uncc.jobboard.user.User
import jakarta.persistence.*

@Entity
data class Token (
        @Id
        @GeneratedValue
        var id: Int ?= null,

        @Column(unique = true)
        var token: String,

        @Enumerated(EnumType.STRING)
        var tokenType: TokenType = TokenType.BEARER,
        var revoked: Boolean = false,
        var expired: Boolean = false,

        @ManyToOne
        @JoinColumn(name = "user_id")
        var user: User
)
