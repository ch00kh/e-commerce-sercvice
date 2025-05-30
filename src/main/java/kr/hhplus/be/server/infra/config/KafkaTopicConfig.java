package kr.hhplus.be.server.infra.config;

import kr.hhplus.be.server.global.event.EventType;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic couponApplyTopic() {
        return TopicBuilder.name(EventType.Topic.COUPON_APPLY)
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic couponUseTopic() {
        return TopicBuilder.name(EventType.Topic.COUPON_USE)
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCreateTopic() {
        return TopicBuilder.name(EventType.Topic.ORDER_CREATE)
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderCompleteTopic() {
        return TopicBuilder.name(EventType.Topic.ORDER_COMPLETE)
                .partitions(4)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderStateTopic() {
        return TopicBuilder.name(EventType.Topic.ORDER_COMPLETE)
                .partitions(4)
                .replicas(1)
                .build();
    }

}
