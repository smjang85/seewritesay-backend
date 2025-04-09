package org.lena.support.test

import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
abstract class BaseIntegrationTest {

    @Autowired
    protected lateinit var webTestClient: WebTestClient

    @Autowired
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var imageRepository: ImageRepository

    protected lateinit var testUser: User
    protected lateinit var testImage: Image

    fun initTestData() {
        userRepository.deleteAll()
        imageRepository.deleteAll()
        testUser = TestDataFactory.createUser(userRepository)
        testImage = TestDataFactory.createImage(imageRepository)
    }
}
