package lv.tsi.bookstore.feature.audit

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.ISBN
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AuditRequest(
    @field:NotNull(message = "Type must be specified")
    val type: AuditType,

    @field:Size(min = 1, message = "At least one entry must be specified")
    val entries: List<AuditRequestEntry>,
)

data class AuditRequestEntry(
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
) {

    @PostMapping
    fun create(@RequestBody @Valid request: AuditRequest) {
        auditService.create(request)
    }
}
