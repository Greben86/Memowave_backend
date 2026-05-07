package dev.greben.memowave.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "`users`")
data class User(
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    @SequenceGenerator(name = "users_generator", sequenceName = "users_seq", allocationSize = 1)
    val id: Long? = null,
    @Column(name = "username", nullable = false)
    @JvmField
    var username: String?,
    @Column(name = "user_role", nullable = false)
    var userRole: String?,
    @Column(name = "password_hash", nullable = false)
    var passwordHash: String?,
    @Column(name = "image_url")
    var imageUrl: String?,
    @Column(name = "email", nullable = false)
    var email: String?,
    @Column(name = "experience", nullable = false)
    var experience: Long
) : UserDetails, AbstractEntity() {

    constructor(): this(username = null, userRole = null, passwordHash = null, imageUrl = null, email = null, experience = 0)

    override fun getUsername(): String? = username

    override fun getPassword(): String? = passwordHash

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf<GrantedAuthority>(SimpleGrantedAuthority(userRole))
}