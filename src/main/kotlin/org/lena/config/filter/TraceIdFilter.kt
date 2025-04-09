package org.lena.config.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import java.util.*

@Component
class TraceIdFilter : Filter {

    companion object {
        const val TRACE_ID = "traceId"
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val traceId = UUID.randomUUID().toString().substring(0, 8)
        MDC.put(TRACE_ID, traceId)

        try {
            chain.doFilter(request, response)
        } finally {
            MDC.remove(TRACE_ID) // 메모리 누수 방지
        }
    }
}