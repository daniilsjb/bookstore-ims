package lv.tsi.bookstore.feature.user

class DuplicateUserException(user: User) :
    RuntimeException("User '${user.username}' already exists.")

interface UserService {

    fun findAll(searchTerm: String? = null): List<User>

    fun findByUsername(username: String): User?

    fun register(user: User): User

    fun toggle(user: User): User

}
