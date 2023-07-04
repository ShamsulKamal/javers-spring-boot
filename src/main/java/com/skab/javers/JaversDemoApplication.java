package com.skab.javers;

import org.javers.spring.auditable.AuthorProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JaversDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(JaversDemoApplication.class, args);
    }

    @Bean
    public AuthorProvider getAuthorProvider() {
        return new SimpleAuthorProvider();
    }

}
