package com.uncc.jobboard.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import java.util.function.Function

@Service
class JwtService {
    fun extractUsername(token: String?): String {
        return extractClaim(token, Function<Claims, String> { obj: Claims -> obj.subject })
    }

    fun <T> extractClaim(token: String?, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    fun generateToken(userDetails: UserDetails): String {
        return generateToken(HashMap(), userDetails)
    }

    fun generateToken(
            extraClaims: Map<String?, Any?>?,
            userDetails: UserDetails
    ): String {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.username)
                .setIssuedAt(Date(System.currentTimeMillis()))
                .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(signInKey, SignatureAlgorithm.HS256)
                .compact()
    }

    fun isTokenValid(token: String?, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String?): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String?): Date {
        return extractClaim(token, Function<Claims, Date> { obj: Claims -> obj.getExpiration() })
    }

    private fun extractAllClaims(token: String?): Claims {
        return Jwts.parserBuilder()
                .setSigningKey(signInKey)
                .build()
                .parseClaimsJws(token)
                .body
    }

    private val signInKey: Key
        private get() {
            val keyBytes: ByteArray = Decoders.BASE64.decode(SECRET_KEY)
            return Keys.hmacShaKeyFor(keyBytes)
        }

    companion object {
        private const val SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
    }
}