package lv.tsi.bookstore.feature.audit

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import lv.tsi.bookstore.feature.book.BookService
import lv.tsi.bookstore.feature.book.NoSuchBookException
import org.hibernate.validator.constraints.ISBN
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CreateAuditRequest(
    @field:NotNull(message = "Type must be specified")
    val type: AuditType,

    @field:Size(min = 1, message = "At least one entry must be specified")
    val entries: List<CreateAuditRequestEntry>,
)

data class CreateAuditRequestEntry(
    @field:ISBN(message = "Invalid ISBN")
    @field:NotNull(message = "ISBN must be specified")
    val isbn: String,

    @field:NotNull(message = "Quantity must be specified")
    @field:Positive(message = "Quantity must be positive")
    val quantity: Int,
)

@RestController
@RequestMapping("/api/v1/audits")
class AuditController(
    private val auditService: AuditService,
    private val bookService: BookService,
) {

    @PostMapping
    fun create(@RequestBody @Valid request: CreateAuditRequest) {
        val audit = Audit(type = request.type)
        val entries = request.entries.map { entry ->
            val book = bookService.findByISBN(entry.isbn) ?: throw NoSuchBookException(entry.isbn)
            AuditEntry(book = book, quantity = entry.quantity)
        }

        audit.entries.addAll(entries)
        auditService.save(audit)
    }
}
