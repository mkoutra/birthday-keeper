package mkoutra.birthdaykeeper.swagger;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Michail E. Koutrakis",
                        email = "mikoutra@yahoo.gr"
                ),
                description = "A simple REST app where a user can insert the date of birth of its friends to remember their their birthday.",
                title = "Birthday-Keeper App - RESTful API",
                version = "1.0",
                license = @License(
                        name = "MIT",
                        url = "https://github.com/mkoutra/birthday-keeper/blob/master/LICENCE"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "Local Environment"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "jwtAuth"
                )
        },
        externalDocs = @ExternalDocumentation(
                description = "GitHub Repository",
                url = "https://github.com/mkoutra/birthday-keeper"
        )
)
@SecurityScheme(
        name = "jwtAuth",
        description = "Provide the JWT token obtained during authentication as a Bearer token.",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
//@Configuration
public class OpenApiConfiguration {
//    @Bean
//    public OpenAPI customOpenAPI() {
//        OpenAPI openAPI = new OpenAPI();
//
//        // Manually add the logout endpoint to Swagger documentation
//        openAPI.path(
//                "/api/logout",
//                new PathItem()
//                        .post(new Operation()
//                                .summary("Logout endpoint")
//                                .description("Performs user logout and invalidates the session")
//                        )
//        );
//        return openAPI;
//    }
}
