package lv.tsi.bookstore.feature.book

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private lateinit var victim: BookRepository

    @Test
    fun `should find books matching by author name`() {
        val results = victim.search("Brandon Sanderson")

        assertThat(results).hasSize(3)
    }

    @Test
    fun `should find books matching by publisher name`() {
        val results = victim.search("DAW Books")

        assertThat(results).hasSize(3)
    }

    @Test
    fun `should find books matching by ISBN`() {
        val results = victim.search("0-312")

        assertThat(results).hasSize(11)
    }

    @Test
    fun `should find books matching by title`() {
        val results = victim.search("dragon")

        assertThat(results).hasSize(2)
    }
}
