package lv.tsi.bookstore.feature.auth

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AuthenticationRequest(
    @field:NotBlank(message = "Username cannot be blank")
    @field:Size(max = 255, message = "Username cannot exceed 255 characters")
    val username: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(max = 255, message = "Password cannot exceed 255 characters")
    val password: String,
)

data class AuthenticationResponse(
    val accessToken: String,
)

@RestController
@RequestMapping("/api/v1/auth")
class AuthenticationController(
    private val service: AuthenticationService
) {

    @PostMapping("/authenticate")
    fun authenticate(@RequestBody @Valid request: AuthenticationRequest): AuthenticationResponse {
        return service.authenticate(request)
    }
}
