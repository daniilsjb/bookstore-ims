package lv.tsi.bookstore.feature.book

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class DefaultBookService(
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val publisherRepository: PublisherRepository,
) : BookService {

    override fun findAll(searchTerm: String?): List<Book> {
        return if (!searchTerm.isNullOrBlank()) {
            bookRepository.search(searchTerm)
        } else {
            bookRepository.findAll(Sort.by("title"))
        }
    }

    override fun findAllPublishers(): List<Publisher> {
        return publisherRepository.findAll()
    }

    override fun findAllAuthors(): List<Author> {
        return authorRepository.findAll()
    }

    override fun findByISBN(isbn: String): Book? {
        return bookRepository.findById(isbn).orElse(null)
    }

    override fun create(book: Book): Book {
        if (bookRepository.existsById(book.isbn)) {
            throw DuplicateBookException(book)
        }

        return bookRepository.save(book)
    }

    override fun update(book: Book): Book {
        return bookRepository.save(book)
    }

    override fun delete(book: Book) {
        try {
            bookRepository.delete(book)
        } catch (e: DataIntegrityViolationException) {
            throw ReferencedBookException(book)
        }
    }
}
