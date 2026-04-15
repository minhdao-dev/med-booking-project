package com.medbooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MedBooking API")
                        .description("Online Medical Booking Platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Nguyen Minh Dao")
                                .email("daonguyenminh.it@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
                .schemaRequirement("Bearer Token", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"));
    }
}