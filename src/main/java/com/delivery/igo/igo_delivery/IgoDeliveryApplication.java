package com.delivery.igo.igo_delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class IgoDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(IgoDeliveryApplication.class, args);
    }

}
