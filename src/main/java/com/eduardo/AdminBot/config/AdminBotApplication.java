package com.eduardo.AdminBot.config; // Ou onde quer que sua classe principal esteja

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.eduardo.AdminBot") // Isso resolve o problema de escaneamento!
public class AdminBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminBotApplication.class, args);
    }

}