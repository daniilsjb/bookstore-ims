package lv.tsi.bookstore.feature.login

import com.vaadin.flow.spring.security.AuthenticationContext
import lv.tsi.bookstore.feature.user.User
import lv.tsi.bookstore.feature.user.UserService
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@ExtendWith(MockitoExtension::class)
class DefaultSecurityServiceTest {

    @Mock
    private lateinit var context: AuthenticationContext

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var victim: DefaultSecurityService

    @Test
    fun `should get user details when authenticated`() {
        val expected = User(
            id = UUID.fromString("ec06a6ad-49d1-4460-a77c-c1c6b35bc2cc"),
            username = "John Doe",
            password = "password"
        )

        `when`(context.getAuthenticatedUser(UserDetails::class.java)).thenReturn(Optional.of(expected))

        val actual = victim.getAuthenticatedUserDetails()

        assertThat(actual).isEqualTo(expected)

        verify(context).getAuthenticatedUser(UserDetails::class.java)
    }

    @Test
    fun `should throw an exception when user is not authenticated`() {
        `when`(context.getAuthenticatedUser(UserDetails::class.java)).thenReturn(Optional.empty())

        assertThatThrownBy { victim.getAuthenticatedUserDetails() }
            .isInstanceOf(NoSuchElementException::class.java)
    }

    @Test
    fun `should get user entity when authenticated`() {
        val expected = User(
            id = UUID.fromString("ec06a6ad-49d1-4460-a77c-c1c6b35bc2cc"),
            username = "John Doe",
            password = "password"
        )

        `when`(context.getAuthenticatedUser(UserDetails::class.java)).thenReturn(Optional.of(expected))
        `when`(userService.findByUsername("John Doe")).thenReturn(expected)

        val actual = victim.getAuthenticatedUser()

        assertThat(actual).isEqualTo(expected)

        verify(context).getAuthenticatedUser(UserDetails::class.java)
        verify(userService).findByUsername("John Doe")
    }

    @Test
    fun `should throw an exception when user entity does not exist`() {
        val expected = User(
            id = UUID.fromString("ec06a6ad-49d1-4460-a77c-c1c6b35bc2cc"),
            username = "John Doe",
            password = "password"
        )

        `when`(context.getAuthenticatedUser(UserDetails::class.java)).thenReturn(Optional.of(expected))
        `when`(userService.findByUsername("John Doe")).thenReturn(null)

        assertThatThrownBy { victim.getAuthenticatedUser() }
            .isInstanceOf(IllegalStateException::class.java)

        verify(context).getAuthenticatedUser(UserDetails::class.java)
        verify(userService).findByUsername("John Doe")
    }
}
