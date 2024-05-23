package lv.tsi.bookstore.feature.audit

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import lv.tsi.bookstore.feature.book.Book
import lv.tsi.bookstore.feature.user.User
import java.io.Serializable
import java.time.LocalDateTime

enum class AuditType(val decrease: Boolean) {
    SALE(decrease = true),
    REFUND(decrease = false),
    SUPPLY(decrease = false),
    WASTAGE(decrease = true);

    fun toDisplayName(): String = name
        .lowercase()
        .replaceFirstChar { it.uppercaseChar() }
}

@Entity
class Audit(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @field:NotNull(message = "Type must be specified")
    @field:Column(nullable = false)
    @field:Enumerated(EnumType.ORDINAL)
    var type: AuditType = AuditType.SALE,

    @field:Column(nullable = false)
    var createdOn: LocalDateTime = LocalDateTime.now(),

    @field:ManyToOne
    @field:JoinColumn(name = "user_id", nullable = true)
    var createdBy: User? = null,

    @field:OneToMany(mappedBy = "audit", fetch = FetchType.EAGER)
    var entries: MutableSet<AuditEntry> = mutableSetOf(),
)

@Entity
class AuditEntry(
    @field:EmbeddedId
    var id: AuditEntryId? = null,

    @field:ManyToOne
    @field:MapsId("auditId")
    @field:JoinColumn(name = "audit_id")
    @field:NotNull(message = "Audit must be specified")
    var audit: Audit? = null,

    @field:ManyToOne
    @field:MapsId("bookIsbn")
    @field:JoinColumn(name = "book_isbn")
    @field:NotNull(message = "Book must be specified")
    var book: Book? = null,

    @field:Column(nullable = false)
    @field:PositiveOrZero(message = "Quantity must not be negative")
    var quantity: Int = 0,
)

@Embeddable
data class AuditEntryId(
    @field:Column(name = "audit_id")
    var auditId: Long,

    @field:Column(name = "book_isbn")
    var bookIsbn: String,
) : Serializable
