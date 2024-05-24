package lv.tsi.bookstore.feature.book

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Sort
import java.util.*

@ExtendWith(MockitoExtension::class)
class DefaultBookServiceTest {

    @Mock
    private lateinit var bookRepository: BookRepository

    @Mock
    private lateinit var authorRepository: AuthorRepository

    @Mock
    private lateinit var publisherRepository: PublisherRepository

    @InjectMocks
    private lateinit var victim: DefaultBookService

    @Test
    fun `should find books by a search term`() {
        `when`(bookRepository.search("Robert Jordan")).thenReturn(listOf())

        val results = victim.findAll("Robert Jordan")

        assertThat(results).isEmpty()

        verify(bookRepository).search("Robert Jordan")
    }

    @Test
    fun `should find all books when search term is null`() {
        val sortingCriteria = Sort.by("title")
        `when`(bookRepository.findAll(sortingCriteria)).thenReturn(listOf())

        val results = victim.findAll(null)

        assertThat(results).isEmpty()

        verify(bookRepository).findAll(sortingCriteria)
    }

    @Test
    fun `should find all books when search term is blank`() {
        val sortingCriteria = Sort.by("title")
        `when`(bookRepository.findAll(sortingCriteria)).thenReturn(listOf())

        val results = victim.findAll("   ")

        assertThat(results).isEmpty()

        verify(bookRepository).findAll(sortingCriteria)
    }

    @Test
    fun `should find all publishers`() {
        `when`(publisherRepository.findAll()).thenReturn(listOf())

        val results = victim.findAllPublishers()

        assertThat(results).isEmpty()

        verify(publisherRepository).findAll()
    }

    @Test
    fun `should find all authors`() {
        `when`(authorRepository.findAll()).thenReturn(listOf())

        val results = victim.findAllAuthors()

        assertThat(results).isEmpty()

        verify(authorRepository).findAll()
    }

    @Test
    fun `should find a book by ISBN successfully`() {
        val isbn = "0-312-85009-3"

        val entity = Book(
            isbn = isbn,
            title = "The Eye of the World",
        )

        `when`(bookRepository.findById(isbn)).thenReturn(Optional.of(entity))

        val result = victim.findByISBN(isbn)

        assertThat(result).isEqualTo(entity)

        verify(bookRepository).findById(isbn)
    }

    @Test
    fun `should return null when finding a non-existent book by ISBN`() {
        val isbn = "0-312-85009-3"

        `when`(bookRepository.findById(isbn)).thenReturn(Optional.empty())

        val result = victim.findByISBN(isbn)

        assertThat(result).isEqualTo(null)

        verify(bookRepository).findById(isbn)
    }

    @Test
    fun `should successfully create a new book`() {
        val entity = Book(
            isbn = "0-312-85009-3",
            title = "The Eye of the World",
        )

        `when`(bookRepository.save(entity)).thenReturn(entity)
        `when`(bookRepository.existsById(entity.isbn)).thenReturn(false)

        val result = victim.create(entity)

        assertThat(result).isEqualTo(entity)

        verify(bookRepository).save(entity)
        verify(bookRepository).existsById(entity.isbn)
    }

    @Test
    fun `should throw an exception when an ISBN is already taken`() {
        val entity = Book(
            isbn = "0-312-85009-3",
            title = "The Eye of the World",
        )

        `when`(bookRepository.existsById(entity.isbn)).thenReturn(true)

        assertThatThrownBy { victim.create(entity) }
            .isInstanceOf(DuplicateBookException::class.java)

        verify(bookRepository).existsById(entity.isbn)
    }

    @Test
    fun `should successfully update an existing book`() {
        val entity = Book(
            isbn = "0-312-85009-3",
            title = "The Eye of the World",
        )

        `when`(bookRepository.save(entity)).thenReturn(entity)

        val result = victim.update(entity)

        assertThat(result).isEqualTo(entity)

        verify(bookRepository).save(entity)
    }

    @Test
    fun `should throw an exception when deleting a referenced book`() {
        val entity = Book(
            isbn = "0-312-85009-3",
            title = "The Eye of the World",
        )

        `when`(bookRepository.delete(entity))
            .thenThrow(DataIntegrityViolationException("Referenced"))

        assertThatThrownBy { victim.delete(entity) }
            .isInstanceOf(ReferencedBookException::class.java)

        verify(bookRepository).delete(entity)
    }
}
