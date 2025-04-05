package org.lena.config.encoding

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CharacterEncodingFilter

@Configuration
class EncodingConfig {

    @Bean
    fun characterEncodingFilter(): CharacterEncodingFilter {
        val filter = CharacterEncodingFilter()
        filter.encoding = "UTF-8"
        filter.setForceEncoding(true)
        return filter
    }
}
