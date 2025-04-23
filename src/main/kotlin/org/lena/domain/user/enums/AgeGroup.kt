package org.lena.domain.user.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class AgeGroup(@get:JsonValue val code: String) {
    UNDER_6("1"),
    AGE_7_TO_9("2"),
    AGE_10_TO_12("3"),
    AGE_13_TO_15("4"),
    AGE_16_TO_18("5"),
    AGE_19_TO_29("6"),
    OVER_30("7"),
    UNKNOWN("0");

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromCode(code: String?): AgeGroup {
            return values().find { it.code == code } ?: UNKNOWN
        }

        fun ofCode(code: String?): AgeGroup {
            return entries.find { it.code == code } ?: UNKNOWN
        }
    }

}
