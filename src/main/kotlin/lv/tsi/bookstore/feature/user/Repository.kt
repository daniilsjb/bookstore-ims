package lv.tsi.bookstore.feature.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, UUID> {

    @Query("""
        SELECT u FROM User u
        WHERE lower(u.username) LIKE lower(concat('%', :searchTerm, '%'))
           OR lower(u.email)    LIKE lower(concat('%', :searchTerm, '%'))
        ORDER BY u.username
    """)
    fun search(searchTerm: String): List<User>

    fun findByUsername(username: String): User?

    fun existsByUsername(username: String): Boolean

}
