package org.lena.domain.user.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.lena.domain.user.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryIntgTest {

    @Autowired
    lateinit var userRepository: UserRepository

    private lateinit var savedUser: User

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()

        savedUser = userRepository.save(
            User.of(
                email = "test@lena.org",
                name = "테스트 유저"
            )
        )
    }

    @Test
    fun `이메일로 사용자 조회 성공`() {
        val foundUser = userRepository.findByEmail("test@lena.org")
        assertNotNull(foundUser)
        assertEquals("테스트 유저", foundUser!!.name)
    }

    @Test
    fun `존재하지 않는 이메일로 조회시 null`() {
        val result = userRepository.findByEmail("notfound@lena.org")
        assertNull(result)
    }

    @Test
    fun `사용자 삭제`() {
        userRepository.delete(savedUser)
        val result = userRepository.findByEmail("test@lena.org")
        assertNull(result)
    }
}
