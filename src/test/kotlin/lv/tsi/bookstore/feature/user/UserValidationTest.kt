package lv.tsi.bookstore.feature.user

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class UserValidationTest {

    private val validator: Validator = Validation
        .buildDefaultValidatorFactory()
        .validator

    @Test
    fun `should pass validation successfully`() {
        val subject = User(
            id = UUID.fromString("aaf2730f-709e-48bf-be4d-c28b838fdf4f"),
            username = "John Doe",
            password = "password",
        )

        assertThat(validator.validate(subject)).isEmpty()
    }

    @Test
    fun `should fail validation when username is blank`() {
        val subject = User(
            id = UUID.fromString("aaf2730f-709e-48bf-be4d-c28b838fdf4f"),
            username = "   ",
            password = "password",
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when password is blank`() {
        val subject = User(
            id = UUID.fromString("aaf2730f-709e-48bf-be4d-c28b838fdf4f"),
            username = "John Doe",
            password = "   ",
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when password is too long`() {
        val subject = User(
            id = UUID.fromString("aaf2730f-709e-48bf-be4d-c28b838fdf4f"),
            username = "John Doe",
            password = "a".repeat(61),
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when email has incorrect format`() {
        val subject = User(
            id = UUID.fromString("aaf2730f-709e-48bf-be4d-c28b838fdf4f"),
            username = "John Doe",
            password = "password",
            email = "invalid",
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }
}
