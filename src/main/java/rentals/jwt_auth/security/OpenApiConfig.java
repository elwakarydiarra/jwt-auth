package rentals.jwt_auth.security;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
          .info(new Info()
            .title("JWT Auth API")
            .version("1.0")
            .description("Documentation de l'API de location sécurisée avec JWT"));
    }
}

