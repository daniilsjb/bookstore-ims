package lv.tsi.bookstore.feature.book

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PublisherValidationTest {

    private val validator: Validator = Validation
        .buildDefaultValidatorFactory()
        .validator

    @Test
    fun `should pass validation successfully`() {
        val subject = Publisher(
            id = 5,
            name = "Tor Books",
        )

        assertThat(validator.validate(subject)).isEmpty()
    }

    @Test
    fun `should fail validation when name is blank`() {
        val subject = Publisher(
            id = 5,
            name = "    ",
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }
}
