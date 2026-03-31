package br.com.study.gameapi;

import br.com.study.genericauthorization.configuration.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {
        "br.com.study.gameapi",
        "br.com.study.genericauthorization"
})
@EnableConfigurationProperties(AppProperties.class)
public class GameApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameApiApplication.class, args);
    }
}