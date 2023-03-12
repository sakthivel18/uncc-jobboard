package com.uncc.jobboard.user

import com.uncc.jobboard.token.Token
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


@Entity
@Table(name = "_user")
data class User (
        @Id
        @GeneratedValue
        val id: Int? = null,
        val firstname: String,
        val lastname: String,
        val email: String,
        private val password: String,

        @Enumerated(EnumType.STRING)
        val role: Role? = null,

        @OneToMany(mappedBy = "user")
        val tokens: List<Token>? = null
) : UserDetails {



    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return listOf(SimpleGrantedAuthority(role?.name))
    }

    override fun getPassword(): String {
        return password!!
    }

    override fun getUsername(): String {
        return email!!
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
