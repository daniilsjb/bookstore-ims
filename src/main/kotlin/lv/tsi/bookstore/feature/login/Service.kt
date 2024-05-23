package lv.tsi.bookstore.feature.login

import lv.tsi.bookstore.feature.user.User
import org.springframework.security.core.userdetails.UserDetails

interface SecurityService {

    fun getAuthenticatedUserDetails(): UserDetails

    fun getAuthenticatedUser(): User

    fun logout()

}
