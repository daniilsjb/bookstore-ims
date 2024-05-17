package lv.tsi.bookstore.feature.user

import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import java.util.function.Supplier

@Service
class DefaultUserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val uuidSupplier: Supplier<UUID>,
) : UserService {

    override fun findAll(searchTerm: String?): List<User> {
        return if (!searchTerm.isNullOrBlank()) {
            userRepository.search(searchTerm)
        } else {
            userRepository.findAll(Sort.by("username"))
        }
    }

    override fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    override fun create(user: User): User {
        if (userRepository.existsByUsername(user.username)) {
            throw DuplicateUserException(user)
        }

        user.id = uuidSupplier.get()
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    override fun toggle(user: User): User {
        user.active = !user.active
        return userRepository.save(user)
    }
}
