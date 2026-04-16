package com.medbooking;

import com.medbooking.config.RsaKeyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(RsaKeyProperties.class)
public class MedbookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedbookingApplication.class, args);
    }

}
