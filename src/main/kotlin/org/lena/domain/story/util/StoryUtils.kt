package org.lena.domain.story.util

object StoryUtils {
    fun sentenceMix(first: String, second: String): String {
        val firstLines = first.lines().map(String::trim).filter(String::isNotEmpty)
        val secondLines = second.lines().map(String::trim).filter(String::isNotEmpty)

        return buildString {
            for (i in 0 until maxOf(firstLines.size, secondLines.size)) {
                if (i < firstLines.size) appendLine(firstLines[i])
                if (i < secondLines.size) appendLine(secondLines[i])
                appendLine()
            }
        }.trim()
    }
}
