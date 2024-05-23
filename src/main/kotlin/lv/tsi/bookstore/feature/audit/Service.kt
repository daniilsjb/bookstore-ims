package lv.tsi.bookstore.feature.audit

import lv.tsi.bookstore.feature.book.Book
import java.time.LocalDate

class InsufficientStock(book: Book) :
    RuntimeException("Cannot decrease '${book.isbn}' due to insufficient stock.")

data class AuditQuery(
    val dateTerm: LocalDate? = null,
    val typeTerm: AuditType? = null,
)

interface AuditService {

    fun findAll(query: AuditQuery = AuditQuery()): List<Audit>

    fun create(audit: Audit)

}
