package lv.tsi.bookstore.feature.user

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.util.UUID

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var victim: UserRepository

    @Test
    fun `should search for users by username or email`() {
        val entity1 = User(
            id = UUID.fromString("72ee1c26-77fb-4fb8-bc34-da6e22ac63a4"),
            username = "Rand al'Thor",
            password = "sewofghi",
            email = "rand@andor.com",
        )

        val entity2 = User(
            id = UUID.fromString("5294aee1-8fce-47ed-99f0-81ff1d903e36"),
            username = "Egwene al'Vere",
            password = "oagidsadpasdf",
        )

        val entity3 = User(
            id = UUID.fromString("be8f97d8-58b1-4a2f-b49d-52680cad9968"),
            username = "Matrim Cauthon",
            password = "qaowuhdads",
            email = "mat@andor.com",
        )

        entityManager.persist(entity1)
        entityManager.persist(entity2)
        entityManager.persist(entity3)

        assertThat(victim.search("al'")).containsOnly(entity1, entity2)
        assertThat(victim.search("andor")).containsOnly(entity1, entity3)
        assertThat(victim.search("z")).isEmpty()
    }

    @Test
    fun `should find user by username when it exists`() {
        val entity = User(
            id = UUID.fromString("72ee1c26-77fb-4fb8-bc34-da6e22ac63a4"),
            username = "Rand al'Thor",
            password = "Callandor123",
        )

        entityManager.persist(entity)

        val actual = victim.findByUsername("Rand al'Thor")

        assertThat(actual).isNotNull()
        assertThat(actual).isEqualTo(entity)
    }

    @Test
    fun `should return null when finding a non-existing user by username`() {
        val actual = victim.findByUsername("Rand al'Thor")

        assertThat(actual).isNull()
    }

    @Test
    fun `should determine an existing user by username`() {
        val entity = User(
            id = UUID.fromString("72ee1c26-77fb-4fb8-bc34-da6e22ac63a4"),
            username = "Rand al'Thor",
            password = "Callandor123",
        )

        entityManager.persist(entity)

        assertThat(victim.existsByUsername("Rand al'Thor")).isTrue()
    }

    @Test
    fun `should determine a non-existing user by username`() {
        assertThat(victim.existsByUsername("Rand al'Thor")).isFalse()
    }
}
