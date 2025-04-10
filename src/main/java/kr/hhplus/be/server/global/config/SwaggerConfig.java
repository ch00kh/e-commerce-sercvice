package kr.hhplus.be.server.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("E-Commerce-Service API")
                        .version("1.0")
                        .description("항해99 백엔드플러스 8기 과제"))
                .servers(List.of(new Server().url("/")));
    }
}
