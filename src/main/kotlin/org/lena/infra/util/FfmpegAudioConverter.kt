package org.lena.infra.util

import mu.KotlinLogging
import org.springframework.web.multipart.MultipartFile
import java.io.File

object FfmpegAudioConverter {
    private val logger = KotlinLogging.logger {}

    /**
     * MultipartFile을 임시 경로에 저장한 후,
     * ffmpeg로 변환하여 resultPath에 저장하고,
     * 변환이 성공하면 임시 파일을 삭제합니다.
     *
     * @param file MultipartFile (업로드된 오디오)
     * @param tempPath 변환 전 저장될 임시 파일 경로
     * @param resultPath 변환 결과가 저장될 최종 경로
     */
    fun convertToMono16k(file: MultipartFile, tempPath: String, resultPath: String) {
        val tempFile = File(tempPath).apply { parentFile.mkdirs() }
        file.transferTo(tempFile)

        val resultFile = File(resultPath).apply { parentFile.mkdirs() }

        logger.debug("FFmpeg 변환 시작: $tempPath -> $resultPath")

        val processBuilder = ProcessBuilder(
            "ffmpeg", "-y",
            "-i", tempPath,
            "-acodec", "pcm_s16le",
            "-ac", "1",
            "-ar", "16000",
            resultPath
        )
        processBuilder.redirectErrorStream(true)

        val process = processBuilder.start()
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            logger.error("FFmpeg 변환 실패 (exit=$exitCode)\n$output")
            throw RuntimeException("FFmpeg 변환 실패 (exit=$exitCode)")
        }

        logger.debug("FFmpeg 변환 완료")

        // 변환 성공 시 temp 파일 삭제
        deleteFileIfExists(tempPath);
    }

    /**
     * 주어진 경로의 파일을 삭제합니다.
     * Azure 평가 후 불필요한 파일을 정리할 때 사용.
     */
    fun deleteFileIfExists(path: String) {
        val file = File(path)
        if (file.exists()) {
            val deleted = file.delete()
            if (deleted) {
                logger.debug("파일 삭제 완료: $path")
            } else {
                logger.warn("파일 삭제 실패: $path")
            }
        } else {
            logger.debug("삭제 시도: 파일이 존재하지 않음: $path")
        }
    }
}
