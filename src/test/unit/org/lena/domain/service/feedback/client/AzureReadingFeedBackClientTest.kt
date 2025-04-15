package org.lena.domain.service.feedback.client

import org.junit.jupiter.api.Test
import org.lena.infra.feedback.client.AzureReadingFeedbackClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import java.io.File

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
class AzureReadingFeedbackClientTest {


    @Autowired
    lateinit var azureReadingFeedbackClient: AzureReadingFeedbackClient

    private val testFilePath = "/tmp/fixed.wav"  // 실제 오디오 파일 경로를 제공해야 합니다.

    @Test
    fun `Azure 발음 피드백 응답 테스트`() {
        // 테스트용 오디오 파일이 존재하는지 확인
        val file = File(testFilePath)
        require(file.exists()) {
            "❌ 테스트용 오디오 파일이 존재하지 않습니다: $testFilePath"
        }

        // 음성 파일을 저장할 경로를 설정합니다.
        val audioFilePath = testFilePath

        // 음성 파일에서 텍스트를 추출합니다. (여기서 텍스트를 음성에서 추출)
        val sentenceFromFile = azureReadingFeedbackClient.speechToText(audioFilePath)

        // 텍스트가 없을 경우 음성에서 추출한 텍스트를 사용하여 발음 평가를 진행합니다.
        val sentence: String? = null // 텍스트가 없을 경우 null을 넣을 수 있습니다.

        // 발음 평가를 수행
        val azureResponse = azureReadingFeedbackClient.evaluatePronunciation(audioFilePath, sentenceFromFile, sentence)

        // 결과 출력
        println("🎯 발음 피드백 결과:")
        println(" - azureResponse: ${azureResponse.toString()}")
        println(" - 정확도: ${azureResponse.accuracyScore}")
        println(" - 유창성: ${azureResponse.fluencyScore}")
        println(" - 완성도: ${azureResponse.completenessScore}")
        println(" - 운율점수: ${azureResponse.pronScore}")
        println(" - 신뢰도: ${azureResponse.confidence}")
        println(" - 녹음에서 추출된 문장: ${azureResponse.sentenceFromFile}")
        println(" - 작문한 문장: ${azureResponse.sentence}")



        // 발음 평가가 정상적으로 이루어졌는지 확인
        assert(azureResponse.accuracyScore >= 0) { "정확도가 0 미만입니다." }
        assert(azureResponse.fluencyScore >= 0) { "유창성이 0 미만입니다." }
        assert(azureResponse.completenessScore >= 0) { "완성도가 0 미만입니다." }

    }
}
