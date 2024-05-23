package lv.tsi.bookstore.feature.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoleTest {

    @Test
    fun `should convert value to display name`() {
        assertThat(Role.MANAGER.toDisplayName()).isEqualTo("Manager")
        assertThat(Role.EMPLOYEE.toDisplayName()).isEqualTo("Employee")
    }

    @Test
    fun `should convert value to role string`() {
        assertThat(Role.MANAGER.toRoleString()).isEqualTo("ROLE_MANAGER")
        assertThat(Role.EMPLOYEE.toRoleString()).isEqualTo("ROLE_EMPLOYEE")
    }
}
