package lv.tsi.bookstore.feature.audit

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuditRequestValidationTest {

    private val validator: Validator = Validation
        .buildDefaultValidatorFactory()
        .validator

    @Test
    fun `should pass validation successfully`() {
        val subject = AuditRequest(
            type = AuditType.SUPPLY,
            entries = listOf(
                AuditRequestEntry(isbn = "0-312-85767-5", quantity = 5),
                AuditRequestEntry(isbn = "978-0-7653-2595-2", quantity = 10),
                AuditRequestEntry(isbn = "0-312-86459-0", quantity = 15),
            )
        )

        assertThat(validator.validate(subject)).isEmpty()
    }

    @Test
    fun `should fail validation when entries are empty`() {
        val subject = AuditRequest(
            type = AuditType.SUPPLY,
            entries = listOf()
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when ISBN is blank`() {
        val subject = AuditRequestEntry(
            isbn = "    ",
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when ISBN has invalid format`() {
        val subject = AuditRequestEntry(
            isbn = "asofdgihi",
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when ISBN has invalid check digit`() {
        val subject = AuditRequestEntry(
            isbn = "0-312-85009-1",
            quantity = 3,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when quantity is negative`() {
        val subject = AuditRequestEntry(
            isbn = "0-312-85009-3",
            quantity = -1,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }

    @Test
    fun `should fail validation when quantity is null`() {
        val subject = AuditRequestEntry(
            isbn = "0-312-85009-3",
            quantity = -1,
        )

        assertThat(validator.validate(subject)).isNotEmpty()
    }
}
