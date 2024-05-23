package lv.tsi.bookstore.feature.audit

import lv.tsi.bookstore.feature.book.Book
import lv.tsi.bookstore.feature.book.BookRepository
import lv.tsi.bookstore.feature.login.SecurityService
import lv.tsi.bookstore.feature.user.User
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*
import java.util.function.Supplier

@Disabled
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
    fun `should successful save a new audit`() {
        val book1 = Book(isbn = "0-312-85009-3", title = "The Eye of the World", quantity = 3)
        val book2 = Book(isbn = "0-312-85140-5", title = "The Great Hunt", quantity = 5)

        val entity = Audit(
            type = AuditType.SALE,
            entries = mutableSetOf(
                AuditEntry(book = book1, quantity = 2),
                AuditEntry(book = book2, quantity = 4),
            )
        )

        val timestamp = LocalDateTime.now()

        val user = User(
            id = UUID.fromString("482de0be-ce3b-4474-9911-027c597f4978"),
            username = "John Doe",
            password = "password",
        )

        val audit = Audit(
            type = AuditType.SALE,
            createdOn = timestamp,
            createdBy = user,
        )

        `when`(timestampSupplier.get()).thenReturn(timestamp)
        `when`(securityService.getAuthenticatedUser()).thenReturn(user)
        `when`(auditRepository.save(audit)).thenReturn(audit)

        victim.create(entity)

        assertThat(entity.createdOn).isEqualTo(timestamp)
        assertThat(entity.createdBy).isEqualTo(user)

        verify(bookRepository).save(book1)
        verify(bookRepository).save(book2)
    }

    @Test
    fun `should throw an exception when a book has insufficient stock`() {
        val book = Book(isbn = "0-312-85009-3", title = "The Eye of the World", quantity = 3)

        val audit = Audit(
            type = AuditType.SALE,
            entries = mutableSetOf(
                AuditEntry(book = book, quantity = 4),
            )
        )

        victim.create(audit)

        assertThatThrownBy { victim.create(audit) }
            .isInstanceOf(InsufficientStock::class.java)
    }
}