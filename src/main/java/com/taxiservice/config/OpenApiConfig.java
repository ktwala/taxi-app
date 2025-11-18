package com.taxiservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI taxiServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8082");
        localServer.setDescription("Local Development Server");

        Server productionServer = new Server();
        productionServer.setUrl("https://api.taxiservice.com");
        productionServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setEmail("support@taxiservice.com");
        contact.setName("Taxi Service Support");
        contact.setUrl("https://www.taxiservice.com");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Taxi Management Service API")
                .version("1.0.0")
                .contact(contact)
                .description("Comprehensive API for managing taxi association operations including " +
                        "member management, financial transactions, levy payments, fines, and disciplinary workflows.")
                .termsOfService("https://www.taxiservice.com/terms")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, productionServer));
    }
}
