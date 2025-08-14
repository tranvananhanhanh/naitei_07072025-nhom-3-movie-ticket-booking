package com.naitei.group3.movie_ticket_booking_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Java Team 3");
        contact.setEmail("example@gmail.com");
        contact.setUrl("example.com");

        Info information = new Info()
                .title("Movie Ticket Booking API Documentation")
                .version("1.0")
                .description("API documentation for the Movie Ticket Booking system, which provides endpoints to manage movies, cinemas, showtimes, bookings.")
                .termsOfService("example.com/terms-of-service")
                .contact(contact);

        return new OpenAPI()
                .info(information)
                .servers(List.of(devServer));
    }
}
