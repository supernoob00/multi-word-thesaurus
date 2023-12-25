package com.somerdin.thesaurus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.somerdin.thesaurus"})
public class ThesaurusApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThesaurusApplication.class, args);
    }
}
