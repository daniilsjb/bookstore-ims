package lv.tsi.bookstore.feature.book

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BookValidationTest {

    private val validator: Validator = Validation
        .buildDefaultValidatorFactory()
        .validator

    @Test
    fun `should pass validation successfully`() {
        val subject = Book(
            isbn = "0-312-85009-3",
            title = "The Eye of the World",
            basePrice = BigDecimal("9.99"),
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isEmpty()
    }

    @Test
    fun `should fail validation when ISBN is blank`() {
        val subject = Book(
            isbn = "    ",
            title = "The Eye of the World",
            basePrice = BigDecimal("9.99"),
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when ISBN has invalid format`() {
        val subject = Book(
            isbn = "asofdgihi",
            title = "The Eye of the World",
            basePrice = BigDecimal("9.99"),
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when ISBN has invalid check digit`() {
        val subject = Book(
            isbn = "0-312-85009-1",
            title = "The Eye of the World",
            basePrice = BigDecimal("9.99"),
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when title is blank`() {
        val subject = Book(
            isbn = "0-312-85009-3",
            title = "             ",
            basePrice = BigDecimal("9.99"),
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when base price is negative`() {
        val subject = Book(
            isbn = "0-312-85009-3",
            title = "The Eye of the World",
            basePrice = BigDecimal("-1"),
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when quantity is negative`() {
        val subject = Book(
            isbn = "0-312-85009-3",
            title = "The Eye of the World",
            basePrice = BigDecimal("9.99"),
            quantity = -1,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }
}
