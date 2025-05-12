package org.example.yhw.bookstorecrud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println( "PasswordEncoder Bean is created. d");
        System.out.println();

        return new BCryptPasswordEncoder();
    }
}
