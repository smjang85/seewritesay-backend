package org.lena.domain.story.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.lena.domain.story.enums.StoryType

@Converter(autoApply = false)
class StoryTypeConverter : AttributeConverter<StoryType, String> {
    override fun convertToDatabaseColumn(attribute: StoryType?): String? {
        return attribute?.code
    }

    override fun convertToEntityAttribute(dbData: String?): StoryType {
        return dbData?.let { StoryType.fromCode(it) } ?: StoryType.SHORT
    }
}