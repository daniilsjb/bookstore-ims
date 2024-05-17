package lv.tsi.bookstore.feature.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

enum class Role(val value: String) {
    EMPLOYEE("ROLE_EMPLOYEE"),
    MANAGER("ROLE_MANAGER"),
}

@Entity
@Table(name = "_user")
class User(
    @field:Id
    var id: UUID? = null,

    @field:Column(nullable = false)
    @field:NotBlank(message = "Username must not be blank")
    private var username: String = "",

    @field:Column(nullable = false, length = 60)
    @field:NotBlank(message = "Password must not be blank")
    private var password: String = "",

    @field:Column(nullable = true)
    @field:Email(message = "Email must have correct format")
    var email: String = "",

    @field:Column(nullable = false)
    @field:Enumerated(EnumType.ORDINAL)
    var role: Role = Role.EMPLOYEE,

    @field:Column(nullable = false)
    var active: Boolean = true,
) : UserDetails {

    fun setUsername(username: String) {
        this.username = username
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getUsername(): String = username
    override fun getPassword(): String = password

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(Role.EMPLOYEE.value))
            .apply { if (role == Role.MANAGER) add(SimpleGrantedAuthority(Role.MANAGER.value)) }
    }

    override fun isCredentialsNonExpired(): Boolean = true
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = active
    override fun isEnabled(): Boolean = true
}

fun UserDetails.hasRole(role: Role): Boolean {
    return authorities.any { it.authority == role.value }
}

fun UserDetails.isEmployee(): Boolean {
    return this.hasRole(Role.EMPLOYEE)
}

fun UserDetails.isManager(): Boolean {
    return this.hasRole(Role.MANAGER)
}
