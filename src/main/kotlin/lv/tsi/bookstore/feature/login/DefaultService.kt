package lv.tsi.bookstore.feature.login

import com.vaadin.flow.spring.security.AuthenticationContext
import lv.tsi.bookstore.feature.user.User
import lv.tsi.bookstore.feature.user.UserService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class DefaultSecurityService(
    private val context: AuthenticationContext,
    private val userService: UserService,
) : SecurityService {

    override fun getAuthenticatedUserDetails(): UserDetails {
        return context
            .getAuthenticatedUser(UserDetails::class.java)
            .get()
    }

    override fun getAuthenticatedUser(): User {
        val username = getAuthenticatedUserDetails().username
        return userService.findByUsername(username)
            ?: error("Authenticated user was not found in the database.")
    }

    override fun logout() {
        context.logout()
    }
}
