package org.lena.infra.feedback.client

import org.springframework.core.io.InputStreamResource
import java.io.InputStream

class MultipartInputStreamFileResource(
    inputStream: InputStream,
    private val filename: String
) : InputStreamResource(inputStream) {

    override fun getFilename(): String = filename

    override fun equals(other: Any?): Boolean {
        return other is MultipartInputStreamFileResource &&
                other.filename == this.filename
    }

    override fun hashCode(): Int = filename.hashCode()
}
