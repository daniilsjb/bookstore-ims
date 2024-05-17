package lv.tsi.bookstore.feature.book

class NoSuchBookException(isbn: String) :
    RuntimeException("Book '${isbn}' does not exist.")

class DuplicateBookException(book: Book) :
    RuntimeException("Book '${book.isbn}' already exists.")

class ReferencedBookException(book: Book) :
    RuntimeException("Book '${book.isbn}' is already referenced.")

interface BookService {

    fun findAll(searchTerm: String? = null): List<Book>

    fun findAllPublishers(): List<Publisher>

    fun findAllAuthors(): List<Author>

    fun findByISBN(isbn: String): Book?

    fun create(book: Book): Book

    fun update(book: Book): Book

    fun delete(book: Book)

}
