package lv.tsi.bookstore.feature.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.security.core.authority.SimpleGrantedAuthority
import java.util.*

class UserTest {

    @Test
    fun `should toggle status appropriately`() {
        val subject = User(
            id = UUID.fromString("aaf2730f-709e-48bf-be4d-c28b838fdf4f"),
            username = "John Doe",
            password = "password",
        )

        assertThat(subject.active).isTrue()
        assertThat(subject.isAccountNonLocked).isTrue()

        subject.toggle()

        assertThat(subject.active).isFalse()
        assertThat(subject.isAccountNonLocked).isFalse()
    }

    @Test
    fun `should have only a single granted authority as an employee`() {
        val subject = User(
            id = UUID.fromString("aaf2730f-709e-48bf-be4d-c28b838fdf4f"),
            username = "John Doe",
            password = "password",
            role = Role.EMPLOYEE,
        )

        val authorities = subject.authorities

        assertThat(authorities).hasSize(1)
        assertThat(authorities).contains(SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        assertThat(subject.hasRole(Role.EMPLOYEE)).isTrue()
    }

    @Test
    fun `should have two granted authorities as a manager`() {
        val subject = User(
            id = UUID.fromString("aaf2730f-709e-48bf-be4d-c28b838fdf4f"),
            username = "John Doe",
            password = "password",
            role = Role.MANAGER,
        )

        val authorities = subject.authorities

        assertThat(authorities).hasSize(2)
        assertThat(authorities).contains(SimpleGrantedAuthority("ROLE_EMPLOYEE"))
        assertThat(authorities).contains(SimpleGrantedAuthority("ROLE_MANAGER"))
        assertThat(subject.hasRole(Role.EMPLOYEE)).isTrue()
        assertThat(subject.hasRole(Role.MANAGER)).isTrue()
    }
}
