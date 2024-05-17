package lv.tsi.bookstore.feature.security

import com.vaadin.flow.spring.security.AuthenticationContext
import lv.tsi.bookstore.feature.user.User
import lv.tsi.bookstore.feature.user.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class SecurityService(
    private val authenticationContext: AuthenticationContext,
    private val userService: UserService,
) {

    fun getAuthenticatedUserDetails(): UserDetails {
        return authenticationContext
            .getAuthenticatedUser(UserDetails::class.java)
            .get()
    }

    fun getAuthenticatedUser(): User {
        val username = getAuthenticatedUserDetails().username
        return userService.findByUsername(username)
            ?: error("Authenticated user was not found in the database.")
    }

    fun logout() {
        authenticationContext.logout()
    }
}
