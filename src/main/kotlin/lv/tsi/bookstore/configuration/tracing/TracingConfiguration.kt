package lv.tsi.bookstore.configuration.tracing

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime
import java.util.*
import java.util.function.Supplier

@Configuration
class TracingConfiguration {

    @Bean
    fun uuid(): Supplier<UUID> {
        return Supplier { UUID.randomUUID() }
    }

    @Bean
    fun time(): Supplier<LocalDateTime> {
        return Supplier { LocalDateTime.now() }
    }
}
