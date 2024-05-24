package lv.tsi.bookstore.feature.audit

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AuditTypeTest {

    @Test
    fun `should convert value to display name`() {
        assertThat(AuditType.SALE.toDisplayName()).isEqualTo("Sale")
        assertThat(AuditType.REFUND.toDisplayName()).isEqualTo("Refund")
        assertThat(AuditType.SUPPLY.toDisplayName()).isEqualTo("Supply")
        assertThat(AuditType.WASTAGE.toDisplayName()).isEqualTo("Wastage")
    }
}
