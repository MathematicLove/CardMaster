package com.example.bankcards;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public final class SecurityTestUtils {

    private SecurityTestUtils() {}

    public static RequestPostProcessor asAdmin() {
        return user("admin").roles("ADMIN");
    }

    public static RequestPostProcessor asUser(long id) {
        return user("user-" + id).roles("USER");
    }
}
