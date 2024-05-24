package lv.tsi.bookstore.feature.audit

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester

@JsonTest
class AuditRequestSerializationTest {

    @Autowired
    private lateinit var tester: JacksonTester<AuditRequest>

    @Test
    fun `should deserialize request from JSON`() {
        val json = """
            {
              "type": "SUPPLY",
              "entries": [
                { "isbn": "0-312-85767-5",     "quantity": 5  },
                { "isbn": "978-0-7653-2595-2", "quantity": 10 },
                { "isbn": "0-312-86459-0",     "quantity": 15 }
              ]
            }
            """.trimIndent()

        val request = AuditRequest(
            type = AuditType.SUPPLY,
            entries = listOf(
                AuditRequestEntry(isbn = "0-312-85767-5", quantity = 5),
                AuditRequestEntry(isbn = "978-0-7653-2595-2", quantity = 10),
                AuditRequestEntry(isbn = "0-312-86459-0", quantity = 15),
            )
        )

        assertThat(tester.parseObject(json)).isEqualTo(request)
    }
}
