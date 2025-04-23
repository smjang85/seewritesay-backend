package org.lena.config.properties.user

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "user.feedback.reset")
class UserFeedbackResetProperties {
    lateinit var schedule: String
    var writingCount: Int = 30
    var readingCount: Int = 5
}