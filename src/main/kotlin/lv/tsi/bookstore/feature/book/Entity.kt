package lv.tsi.bookstore.feature.book

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.ISBN
import java.math.BigDecimal

@Entity
class Author(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @field:Column(nullable = false, unique = true)
    @field:NotBlank(message = "Name must not be blank")
    var name: String,
)

@Entity
class Publisher(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @field:Column(nullable = false, unique = true)
    @field:NotBlank(message = "Name must not be blank")
    var name: String,
)

@Entity
class Book(
    @field:Id
    @field:NotBlank(message = "ISBN must not be blank")
    @field:ISBN(type = ISBN.Type.ANY, message = "Invalid ISBN")
    var isbn: String = "",

    @field:Column(nullable = false)
    @field:NotBlank(message = "Title must not be blank")
    @field:Size(max = 255, message = "Title cannot be longer than 255 characters")
    var title: String = "",

    @field:Column(nullable = false)
    @field:NotNull(message = "Base price must be specified")
    @field:PositiveOrZero(message = "Base price cannot be negative")
    var basePrice: BigDecimal = BigDecimal.ZERO,

    @field:Column(nullable = false)
    @field:PositiveOrZero(message = "Quantity cannot be negative")
    var quantity: Int = 0,

    @field:ManyToOne
    @field:JoinColumn(name = "publisher_id", nullable = true)
    var publisher: Publisher? = null,

    @field:ManyToMany(fetch = FetchType.EAGER)
    @field:JoinTable(
        name = "authorship",
        joinColumns = [JoinColumn(name = "book_isbn")],
        inverseJoinColumns = [JoinColumn(name = "author_id")],
    )
    var authors: MutableSet<Author> = mutableSetOf(),
)
