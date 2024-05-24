package lv.tsi.bookstore.feature.audit

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Root
import lv.tsi.bookstore.feature.book.BookRepository
import lv.tsi.bookstore.feature.book.NoSuchBookException
import lv.tsi.bookstore.feature.login.SecurityService
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.function.Supplier

@Service
class DefaultAuditService(
    private val auditRepository: AuditRepository,
    private val entryRepository: AuditEntryRepository,
    private val bookRepository: BookRepository,
    private val securityService: SecurityService,
    private val timestampSupplier: Supplier<LocalDateTime>,
) : AuditService {

    override fun findAll(query: AuditQuery): List<Audit> {
        val (dateTerm, typeTerm) = query

        val hasDate = Specification { root: Root<Audit>, _: CriteriaQuery<*>, cb: CriteriaBuilder ->
            dateTerm?.let {
                val start = dateTerm.atStartOfDay()
                cb.between(root.get("createdOn"), start, start.plusDays(1L))
            } ?: cb.conjunction()
        }
        val hasType = Specification { root: Root<Audit>, _: CriteriaQuery<*>, cb: CriteriaBuilder ->
            typeTerm?.let {
                cb.equal(root.get<AuditType>("type"), typeTerm)
            } ?: cb.conjunction()
        }

        return auditRepository.findAll(
            Specification.where(hasDate).and(hasType)
        )
    }

    override fun create(audit: Audit) {
        // Ensure that the stock levels are sufficient for the operation.
        if (audit.type.decrease) {
            for (entry in audit.entries) {
                val book = entry.book ?: error("Expected a non-null book.")
                val availableQuantity = book.quantity
                val decreasedQuantity = entry.quantity
                if (decreasedQuantity > availableQuantity) {
                    throw InsufficientStockException(book)
                }
            }
        }

        // Create the parent entity with automatically populated values.
        var entity = Audit(
            type = audit.type,
            createdOn = timestampSupplier.get(),
            createdBy = securityService.getAuthenticatedUser(),
        )

        // Persist the parent object to obtain its identifier.
        entity = auditRepository.save(entity)

        // Create entry entities corresponding to the audit.
        val entries = mutableListOf<AuditEntry>()
        for (entry in audit.entries) {
            val book = entry.book ?: error("Expected a non-null book.")
            val id = AuditEntryId(entity.id, book.isbn)
            entries.add(AuditEntry(id, entity, book, entry.quantity))
        }

        // Persist the entries.
        entryRepository.saveAll(entries)

        // Increase or decrease book quantities appropriately.
        for (entry in entries) {
            val book = entry.book ?: error("Expected a non-null book.")
            if (audit.type.decrease) {
                book.quantity -= entry.quantity
            } else {
                book.quantity += entry.quantity
            }

            bookRepository.save(book)
        }
    }

    override fun create(request: AuditRequest) {
        val audit = Audit(type = request.type)
        val entries = request.entries.map { entry ->
            val book = bookRepository
                .findById(entry.isbn)
                .orElseThrow { NoSuchBookException(entry.isbn) }

            AuditEntry(book = book, quantity = entry.quantity)
        }

        audit.entries.addAll(entries)
        create(audit)
    }
}
