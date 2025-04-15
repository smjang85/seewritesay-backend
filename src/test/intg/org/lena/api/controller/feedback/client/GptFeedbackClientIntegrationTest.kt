package org.lena.api.controller.feedback.client

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.lena.api.common.dto.ApiResponse
import org.lena.api.dto.feedback.ai.writing.AiWritingFeedbackRequestDto

import org.lena.domain.image.entity.Image
import org.lena.domain.image.repository.ImageRepository
import org.lena.domain.user.entity.User
import org.lena.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import kotlin.jvm.java


@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class GptFeedbackIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    // 테스트용 더미 유저 & 이미지. 실제 프로젝트에선 테스트 픽스처 또는 @BeforeEach에서 세팅 필요.
    private lateinit var testUser: User
    private lateinit var testImage: Image




    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var imageRepository: ImageRepository

    @BeforeEach
    fun setUp() {
        val existingUser = userRepository.findByEmail("test@lena.org")
        testUser = existingUser ?: userRepository.save(User.of(email = "test@lena.org", name = "테스트유저"))


    }

    @Test
    @DisplayName("GPT 피드백 자동 생성 및 등급 검증 테스트 (100개 이상)")
    fun generateGptFeedback_withGradeValidation_collectErrors() {
        val sentenceGradeMap = mapOf(
            "guguhugugug" to "F",
            "asdf qwer" to "F",
            "1234567" to "F",
            "girl walk" to "D", // 단어 나열, 문법 오류
            "He run" to "D", // 문법 오류
            "The girl is green dress" to "D", // 'in' 생략 등 문법 오류

            "A girl is walking in the rain" to "B", // 기본 묘사 O, 어휘 단순
            "She holds an umbrella" to "C", // 문법 OK, 하지만 맥락 부족
            "The girl is walking with a yellow umbrella in a park" to "A", // 묘사 O, 배경 포함

            "A young girl walks with a smile in the rain" to "A", // 감정 표현 포함
            "A little girl in a green dress walks through a daisy field holding a yellow umbrella." to "A", // 구체적이고 묘사 풍부
            "Holding a yellow umbrella, the girl walks peacefully through the flower-filled meadow as light rain falls around her." to "A", // 문장 다양성, 묘사 우수

            "She is in the park." to "C", // 문법 OK, 다소 단순
            "A girl runs fast." to "C", // 문법 OK, 약간 생동감 있음
            "The child is holding a yellow umbrella under the rain." to "B", // 정확하고 묘사 있음
            "It is raining and the girl is smiling." to "B", // 비와 감정 묘사 포함
            "The little girl, with joy in her eyes, skips along the daisy path in a green dress." to "A", // 묘사력 매우 좋음
            "A joyful girl in a bright green dress dances in the gentle rain with a yellow umbrella." to "A", // 생생한 묘사
            "Dressed in green, the girl twirls through a field of daisies as the rain softly falls." to "A", // 문장 구성 다양, 배경 + 동작 묘사

            "She go walk in rain." to "D", // 문법 오류
            "Walking in rain girl green dress." to "F", // 의미 파악 어려움
            "짧고 이상한 문장입니다." to "F", // 한국어 문장

            "Rain is falling." to "C", // 너무 단순, 이미지 연결성 낮음
            "The girl wear a green dress." to "D", // 문법 오류(wear → wears/is wearing)
            "A small child walking with umbrella." to "C", // 문법 다소 미흡, 내용 명확
            "There is a girl and she walks." to "C", // 자연스러움 낮음, 서술 나열
            "A little girl joyfully walks through a field of white flowers." to "A", // 명확하고 묘사력 좋음
            "In the rain, a happy girl dances under her umbrella." to "A", // 분위기 + 감정 O
            "A young girl strolls a winding path, surrounded by daisies, under soft rain." to "A", // 풍경 중심 묘사 좋음
            "A girl walking." to "D", // 현재분사만, 부족한 표현
            "The girl is rain." to "F", // 의미 불명확
            "푸른 들판에 소녀가 걸어간다." to "F" // 한국어 입력
        )



        val failures = mutableListOf<String>()

        println("총 ${sentenceGradeMap.size}개의 문장에 대해 등급 테스트를 진행합니다.")
        println("────────────────────────────")

        sentenceGradeMap.entries.forEachIndexed { index, (sentence, expectedGrade) ->
            val request = AiWritingFeedbackRequestDto(sentence = sentence, imageId = 1)

            val response = webTestClient.post()
                .uri("/api/v1/ai/feedback/generate")
                .headers { it.setBasicAuth(testUser.email, "dummy") }
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk
                .expectBody(ApiResponse::class.java)
                .returnResult()
                .responseBody

            val data = response?.data as? Map<*, *>
            val actualGrade = data?.get("grade") as? String

            if (expectedGrade == actualGrade) {
                println("[$index] ✅ '$sentence' → 예상: $expectedGrade / 실제: $actualGrade")
            } else {
                println("[$index] ❌ '$sentence' → 예상: $expectedGrade / 실제: $actualGrade")
                failures.add("[$index] ❌ '$sentence' → 예상: $expectedGrade / 실제: $actualGrade")
            }
        }

        println("────────────────────────────")
        println("총 실패 수: ${failures.size}")
        if (failures.isNotEmpty()) {
            failures.forEach { println(it) }
            throw IllegalStateException("등급 검증 실패 항목이 존재합니다.")
        } else {
            println("모든 테스트가 성공했습니다.")
        }
    }



}
