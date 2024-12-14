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
public class OpenApiConfiguration {
}
