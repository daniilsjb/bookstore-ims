package lv.tsi.bookstore.feature.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

enum class Role {
    EMPLOYEE,
    MANAGER;

    fun toRoleString(): String = "ROLE_$name"

    fun toDisplayName(): String = name
        .lowercase()
        .replaceFirstChar { it.uppercaseChar() }
}

@Entity
@Table(name = "_user")
class User(
    @field:Id
    var id: UUID? = null,

    @field:Column(nullable = false, unique = true)
    @field:NotBlank(message = "Username must not be blank")
    @field:Size(max = 255, message = "Username cannot be longer than 255 characters")
    private var username: String = "",

    @field:Column(nullable = false, length = 60)
    @field:NotBlank(message = "Password must not be blank")
    @field:Size(max = 60, message = "Password cannot be longer than 60 characters")
    private var password: String = "",

    @field:Column(nullable = true)
    @field:Email(message = "Email must have correct format")
    var email: String? = null,

    @field:NotNull(message = "Role must be specified")
    @field:Column(nullable = false)
    @field:Enumerated(EnumType.ORDINAL)
    var role: Role = Role.EMPLOYEE,

    @field:Column(nullable = false)
    var active: Boolean = true,
) : UserDetails {

    fun toggle() {
        active = !active
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getUsername(): String = username
    override fun getPassword(): String = password

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(Role.EMPLOYEE.toRoleString()))
            .apply { if (role == Role.MANAGER) add(SimpleGrantedAuthority(Role.MANAGER.toRoleString())) }
    }

    override fun isCredentialsNonExpired(): Boolean = true
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = active
    override fun isEnabled(): Boolean = active
}

fun UserDetails.hasRole(role: Role): Boolean =
    authorities.any { it.authority == role.toRoleString() }
