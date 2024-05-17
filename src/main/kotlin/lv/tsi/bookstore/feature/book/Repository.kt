package lv.tsi.bookstore.feature.book

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository : JpaRepository<Author, Long>

@Repository
interface PublisherRepository : JpaRepository<Publisher, Long>

@Repository
interface BookRepository : JpaRepository<Book, String> {

    @Query("""
        SELECT DISTINCT b FROM Book b
            LEFT JOIN b.publisher p
            LEFT JOIN b.authors a
        WHERE lower(b.isbn)  LIKE lower(concat('%', :searchTerm, '%'))
           OR lower(b.title) LIKE lower(concat('%', :searchTerm, '%'))
           OR lower(a.name)  LIKE lower(concat('%', :searchTerm, '%'))
           OR lower(p.name)  LIKE lower(concat('%', :searchTerm, '%'))
        ORDER BY b.title
    """)
    fun search(searchTerm: String): List<Book>

}
