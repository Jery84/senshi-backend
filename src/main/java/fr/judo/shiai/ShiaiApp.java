package fr.judo.shiai;

import fr.judo.shiai.service.LoaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ShiaiApp implements ApplicationRunner {

    @Autowired
    private LoaderService loaderService;

    public static void main(String[] args) {
        SpringApplication.run(ShiaiApp.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        loaderService.load();
    }
}
