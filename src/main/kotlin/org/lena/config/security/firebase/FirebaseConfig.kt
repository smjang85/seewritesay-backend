package org.lena.config.security.firebase


import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct
import java.io.FileInputStream
import java.io.FileNotFoundException

@Configuration
class FirebaseConfig {

    @PostConstruct
    fun init() {
        if (FirebaseApp.getApps().isEmpty()) {
            val inputStream = javaClass.classLoader.getResourceAsStream("firebase-service-account.json")
                ?: throw FileNotFoundException("firebase-service-account.json not found in classpath")

            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .build()

            FirebaseApp.initializeApp(options)
        }
    }
}
