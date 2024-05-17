package lv.tsi.bookstore.feature.user

class DuplicateUserException(user: User) :
    RuntimeException("User with username '${user.username}' already exists")

interface UserService {

    fun findAll(searchTerm: String? = null): List<User>

    fun findByUsername(username: String): User?

    fun create(user: User): User

    fun toggle(user: User): User

}
