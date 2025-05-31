package kr.hhplus.be.server.surpport.container;

import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

@Configuration
public class KafkaTestcontainersConfiguration {

    public static final KafkaContainer KAFKA_CONTAINER;

    static {
        KAFKA_CONTAINER = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka"));
        KAFKA_CONTAINER.setPortBindings(List.of("9092:9092"));
        KAFKA_CONTAINER.start();

        // 카프카 접속 정보를 시스템 프로퍼티로 설정
        System.setProperty("spring.kafka.bootstrap-servers", KAFKA_CONTAINER.getBootstrapServers());
    }

    @PreDestroy
    public void preDestroy() {
        if (KAFKA_CONTAINER.isRunning()) {
            KAFKA_CONTAINER.stop();
        }
    }
}
