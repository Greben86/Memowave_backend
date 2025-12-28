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
    var id: Long = 0L,
    @Column(name = "username", nullable = false)
    private var username: String?,
    @Column(name = "user_role", nullable = false)
    private var userRole: String?,
    @Column(name = "password_hash", nullable = false)
    private var passwordHash: String?,
    @Column(name = "image_url", nullable = false)
    private var imageUrl: String?,
    @Column(name = "email", nullable = false)
    private var email: String?,
) : UserDetails, AbstractEntity() {

    constructor(): this(username = null, userRole = null, passwordHash = null, imageUrl = null, email = null)

    override fun getUsername(): String? = username

    override fun getPassword(): String? = passwordHash

    fun getUserRole(): String? = userRole

    fun setUserRole(userRole: String) {
        this.userRole = userRole
    }

    fun getImageUrl(): String? = imageUrl

    fun getEmail(): String? = email

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf<GrantedAuthority>(SimpleGrantedAuthority(userRole))
}