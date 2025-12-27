package dev.greben.memowave.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.List

@Entity
@Table(name = "`users`")
data class User(
    @Column(name = "username", nullable = false)
    private var username: String,
    @Column(name = "user_role", nullable = false)
    private var userRole: String,
    @Column(name = "password_hash", nullable = false)
    private var passwordHash: String,
    @Column(name = "image_url", nullable = false)
    private var imageUrl: String,
    @Column(name = "email", nullable = false)
    private var email: String,
) : UserDetails, AbstractEntity() {

    override fun getUsername(): String = username

    override fun getPassword(): String = passwordHash

    fun getUserRole(): String = userRole

    override fun getAuthorities(): MutableCollection<out GrantedAuthority?> {
        return List.of<GrantedAuthority?>(SimpleGrantedAuthority(userRole))
    }
}