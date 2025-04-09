package org.lena.support.fixture

import org.lena.domain.user.entity.User

object UserFixture {
    fun create(
        name: String = "테스트유저",
        email: String = "test@example.com"
    ): User {
        return User(
            name = name,
            email = email
        )
    }
}