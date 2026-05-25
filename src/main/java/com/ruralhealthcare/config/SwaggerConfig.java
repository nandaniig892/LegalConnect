package com.ruralhealthcare.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ruralHealthcareOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Rural Healthcare Accessibility Platform API")
                        .description("Full-stack telemedicine and rural healthcare access platform. " +
                                     "Supports JWT-authenticated RBAC for ADMIN, DOCTOR, PATIENT, and PHARMACY roles.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Rural Healthcare Team")
                                .email("admin@ruralhealthcare.in"))
                        .license(new License().name("MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your JWT access token. Obtain it from POST /api/v1/auth/login")));
    }
}
