package lv.tsi.bookstore.feature.auth

interface AuthenticationService {

    fun authenticate(request: AuthenticationRequest): AuthenticationResponse

}
