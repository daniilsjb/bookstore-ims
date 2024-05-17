package lv.tsi.bookstore.feature.auth

import lv.tsi.bookstore.configuration.security.JwtService
import lv.tsi.bookstore.feature.user.User
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class DefaultAuthenticationService(
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
) : AuthenticationService {

    override fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                request.username,
                request.password,
            )
        )

        val user = authentication.principal as User
        return AuthenticationResponse(
            accessToken = jwtService.generateAccessToken(user),
        )
    }
}
