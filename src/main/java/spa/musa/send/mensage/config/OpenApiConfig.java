package spa.musa.send.mensage.config;

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
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Musa SPA API")
                        .version("1.0.0")
                        .description("API para gerenciamento do Musa SPA - Agendamentos, Clientes, Serviços, Campanhas e Integração WhatsApp")
                        .contact(new Contact()
                                .name("Musa SPA")
                                .email("contato@musasp.com.br"))
                        .license(new License()
                                .name("Proprietary")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor de Desenvolvimento")
                ));
    }
}
