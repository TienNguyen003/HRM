package com.hrm.Webconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/** Tách riêng để AuthenticationService không tạo vòng lặp với SecurityConfig. */
@Configuration
public class PasswordConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
