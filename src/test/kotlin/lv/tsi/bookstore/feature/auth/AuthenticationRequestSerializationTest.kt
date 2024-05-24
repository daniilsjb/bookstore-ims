package lv.tsi.bookstore.feature.auth

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester

@JsonTest
class AuthenticationRequestSerializationTest {

    @Autowired
    private lateinit var tester: JacksonTester<AuthenticationRequest>

    @Test
    fun `should deserialize request from JSON`() {
        val json = """
            {
              "username": "John Doe",
              "password": "password"
            }
            """.trimIndent()

        val request = AuthenticationRequest(
            username = "John Doe",
            password = "password",
        )

        assertThat(tester.parseObject(json)).isEqualTo(request)
    }
}
