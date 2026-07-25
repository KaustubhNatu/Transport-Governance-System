package com.tgs.transport_governance_api.config;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    @SecurityRequirement(name = "bearerAuth")
    @Bean
    public OpenAPI transportGovernanceAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Transport Governance System API")
                        .description("Backend API for Transport Governance System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Kaustubh Natu"))
                        .license(new License()
                                .name("MIT License")))
                .externalDocs(new ExternalDocumentation()
                        .description("Project Documentation"));
    }
}


