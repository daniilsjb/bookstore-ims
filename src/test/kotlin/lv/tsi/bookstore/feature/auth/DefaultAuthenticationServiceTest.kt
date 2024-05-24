package lv.tsi.bookstore.feature.auth

import lv.tsi.bookstore.configuration.security.JwtService
import lv.tsi.bookstore.feature.user.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class DefaultAuthenticationServiceTest {

    @Mock
    private lateinit var jwtService: JwtService

    @Mock
    private lateinit var authenticationManager: AuthenticationManager

    @InjectMocks
    private lateinit var victim: DefaultAuthenticationService

    @Test
    fun `should authenticate user successfully`() {
        val user = User(
            id = UUID.fromString("6d5960ed-e0af-45fb-87bc-fb8658379f7d"),
            username = "John Doe",
            password = "password",
        )

        val token = UsernamePasswordAuthenticationToken(
            user.username,
            user.password,
        )

        val authentication = UsernamePasswordAuthenticationToken(user, user.username)

        `when`(authenticationManager.authenticate(token)).thenReturn(authentication)
        `when`(jwtService.generateAccessToken(user)).thenReturn("access")

        val result = victim.authenticate(AuthenticationRequest("John Doe", "password"))

        assertThat(result.accessToken).isEqualTo("access")

        verify(authenticationManager).authenticate(token)
        verify(jwtService).generateAccessToken(user)
    }
}
