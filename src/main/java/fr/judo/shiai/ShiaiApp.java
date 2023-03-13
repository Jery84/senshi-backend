package fr.judo.shiai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ShiaiApp {

    public static void main(String[] args) {
        SpringApplication.run(ShiaiApp.class, args);
    }
}
