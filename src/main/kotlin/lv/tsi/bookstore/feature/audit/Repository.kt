package lv.tsi.bookstore.feature.audit

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface AuditRepository : JpaRepository<Audit, Long>, JpaSpecificationExecutor<Audit>

@Repository
interface AuditEntryRepository : JpaRepository<AuditEntry, AuditEntryId>
