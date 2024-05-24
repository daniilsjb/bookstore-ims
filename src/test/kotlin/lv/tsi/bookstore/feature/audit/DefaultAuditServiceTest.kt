package lv.tsi.bookstore.feature.audit

import lv.tsi.bookstore.feature.book.Book
import lv.tsi.bookstore.feature.book.BookRepository
import lv.tsi.bookstore.feature.login.SecurityService
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.function.Supplier

@ExtendWith(MockitoExtension::class)
class DefaultAuditServiceTest {

    @Mock
    private lateinit var auditRepository: AuditRepository

    @Mock
    private lateinit var entryRepository: AuditEntryRepository

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var securityService: SecurityService

    @Mock
    private lateinit var timestampSupplier: Supplier<LocalDateTime>

    @InjectMocks
    private lateinit var victim: DefaultAuditService

    @Test
    fun `should throw an exception when a book has insufficient stock`() {
        val book = Book(isbn = "0-312-85009-3", title = "The Eye of the World", quantity = 3)

        val audit = Audit(
            type = AuditType.SALE,
            entries = mutableSetOf(
                AuditEntry(book = book, quantity = 4),
            )
        )

        assertThatThrownBy { victim.create(audit) }
            .isInstanceOf(InsufficientStockException::class.java)
    }
}
