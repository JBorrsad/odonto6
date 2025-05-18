package odoonto.documentation.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

/**
 * Configuración de OpenAPI para documentación de API
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configura el servicio de documentación OpenAPI
     */
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Odoonto")
                        .description("API REST Odoonto")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Juan Borrás")
                                .url("sadaborras63@gmail.com")
                                .email("juan-borras@hotmail.com")));
    }
} 