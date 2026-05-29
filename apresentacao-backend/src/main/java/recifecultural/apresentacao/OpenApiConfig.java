package recifecultural.apresentacao;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Recife Cultural API")
                        .description("Sistema de gestão de eventos culturais — 2ª Entrega")
                        .version("0.0.1-SNAPSHOT"));
    }

    @Bean
    public GroupedOpenApi apiGroup() {
        return GroupedOpenApi.builder()
                .group("api")
                .displayName("API — REST por Agregado")
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/bff/**")
                .build();
    }

    @Bean
    public GroupedOpenApi bffGroup() {
        return GroupedOpenApi.builder()
                .group("bff")
                .displayName("BFF — Endpoints UI-Oriented")
                .pathsToMatch("/api/bff/**")
                .build();
    }
}
