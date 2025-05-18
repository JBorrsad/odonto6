package odoonto.presentation.documentation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * Configuración de Swagger para documentación de API
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * Configura el servicio de documentación Swagger
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("odoonto.presentation.rest.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    /**
     * Información general de la API
     */
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Odoonto API",
                "API REST para el sistema odontológico Odoonto",
                "1.0.0",
                "Términos del servicio",
                new Contact("Equipo Odoonto", "https://www.odoonto.com", "info@odoonto.com"),
                "Licencia",
                "URL de la licencia",
                Collections.emptyList()
        );
    }
} 