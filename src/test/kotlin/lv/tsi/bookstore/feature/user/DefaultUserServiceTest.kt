package lv.tsi.bookstore.feature.user

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import java.util.function.Supplier

@ExtendWith(MockitoExtension::class)
class DefaultUserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var uuidSupplier: Supplier<UUID>

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    private lateinit var victim: DefaultUserService

    @Test
    fun `should find users by a search term`() {
        `when`(userRepository.search("john")).thenReturn(listOf())

        val results = victim.findAll("john")

        assertThat(results).isEmpty()

        verify(userRepository).search("john")
    }

    @Test
    fun `should find all users when search term is null`() {
        val sortingCriteria = Sort.by("username")
        `when`(userRepository.findAll(sortingCriteria)).thenReturn(listOf())

        val results = victim.findAll(null)

        assertThat(results).isEmpty()

        verify(userRepository).findAll(sortingCriteria)
    }

    @Test
    fun `should find all users when search term is blank`() {
        val sortingCriteria = Sort.by("username")
        `when`(userRepository.findAll(sortingCriteria)).thenReturn(listOf())

        val results = victim.findAll("   ")

        assertThat(results).isEmpty()

        verify(userRepository).findAll(sortingCriteria)
    }

    @Test
    fun `should successfully find a user by username`() {

        val entity = User(
            id = UUID.fromString("3e41d3b9-f0e7-4360-9412-d95cdb147f81"),
            username = "John Doe",
            password = "password",
        )

        `when`(userRepository.findByUsername("John Doe")).thenReturn(entity)

        val result = victim.findByUsername("John Doe")

        assertThat(result).isEqualTo(entity)

        verify(userRepository).findByUsername("John Doe")
    }

    @Test
    fun `should successfully register a new user`() {
        val entity = User(
            username = "John Doe",
            password = "password",
        )

        val id = UUID.fromString("415185e2-1ba4-4e55-8766-2b76507d8da9")

        `when`(uuidSupplier.get()).thenReturn(id)
        `when`(passwordEncoder.encode("password")).thenReturn("encoded")
        `when`(userRepository.existsByUsername("John Doe")).thenReturn(false)
        `when`(userRepository.save(entity)).thenReturn(entity)

        victim.register(entity)

        assertThat(entity.id).isEqualTo(id)
        assertThat(entity.password).isEqualTo("encoded")

        verify(uuidSupplier).get()
        verify(userRepository).save(entity)
        verify(userRepository).existsByUsername("John Doe")
        verify(passwordEncoder).encode("password")
    }

    @Test
    fun `should throw an exception when a username is already taken`() {
        val entity = User(
            username = "John Doe",
            password = "password",
        )

        `when`(userRepository.existsByUsername("John Doe")).thenReturn(true)

        assertThatThrownBy { victim.register(entity) }
            .isInstanceOf(DuplicateUserException::class.java)

        assertThat(entity.id).isEqualTo(null)

        verify(userRepository).existsByUsername("John Doe")
    }

    @Test
    fun `should toggle the system access status of a user`() {
        val entity = User(
            username = "John Doe",
            password = "password",
            active = true,
        )

        `when`(userRepository.save(entity)).thenReturn(entity)

        victim.toggle(entity)

        assertThat(entity.active).isEqualTo(false)

        verify(userRepository).save(entity)
    }
}
