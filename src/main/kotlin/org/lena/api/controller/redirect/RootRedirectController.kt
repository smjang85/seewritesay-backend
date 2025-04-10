package org.lena.api.controller.redirect

import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootRedirectController {

    @GetMapping("/")
    fun redirectToGithub(response: HttpServletResponse) {
        response.sendRedirect("https://github.com/smjang85")
    }
}