package br.com.study.gameapi;

import br.com.study.genericauthorization.configuration.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@ComponentScan(basePackages = {
        "br.com.study.gameapi",
        "br.com.study.genericauthorization"
})
public class GameApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameApiApplication.class, args);
    }
}