package lv.tsi.bookstore.feature.auth

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuthenticationRequestValidationTest {

    private val validator: Validator = Validation
        .buildDefaultValidatorFactory()
        .validator

    @Test
    fun `should pass validation successfully`() {
        val subject = AuthenticationRequest(
            username = "John Doe",
            password = "password",
        )

        assertThat(validator.validate(subject)).isEmpty()
    }

    @Test
    fun `should fail validation when username is blank`() {
        val subject = AuthenticationRequest(
            username = "   ",
            password = "password",
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when username is too long`() {
        val subject = AuthenticationRequest(
            username = "a".repeat(256),
            password = "password",
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when password is blank`() {
        val subject = AuthenticationRequest(
            username = "John Doe",
            password = "   ",
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when password is too long`() {
        val subject = AuthenticationRequest(
            username = "John Doe",
            password = "a".repeat(256),
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }
}
