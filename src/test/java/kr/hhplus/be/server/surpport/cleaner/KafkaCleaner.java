package kr.hhplus.be.server.surpport.cleaner;

import kr.hhplus.be.server.global.event.EventType;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Component
public class KafkaCleaner {

    private final AdminClient adminClient;
    private final List<String> managedTopics;

    @Autowired
    public KafkaCleaner(KafkaProperties kafkaProperties) {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                    kafkaProperties.getBootstrapServers());
        this.adminClient = AdminClient.create(configs);

        this.managedTopics = Arrays.stream(EventType.values()).map(EventType::getTopic).toList();
    }

    public void clear() {
        try {
            resetTopics();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to clear Kafka topics", e);
        }
    }

    private void resetTopics() throws ExecutionException, InterruptedException {
        // 존재하는 토픽 확인
        Set<String> existingTopics = adminClient.listTopics().names().get();
        
        // 관리 대상 토픽 중 존재하는 것만 처리
        List<String> topicsToReset = managedTopics.stream()
                .filter(existingTopics::contains)
                .toList();
        
        if (!topicsToReset.isEmpty()) {
            Map<TopicPartition, RecordsToDelete> recordsToDelete = new HashMap<>();
            
            // 각 토픽의 파티션 정보 조회
            for (String topic : topicsToReset) {
                TopicDescription topicDescription =
                        adminClient.describeTopics(Collections.singleton(topic)).all().get().get(topic);
                
                // 모든 파티션에 대해 처리
                for (TopicPartitionInfo partitionInfo : topicDescription.partitions()) {
                    TopicPartition topicPartition = new TopicPartition(
                            topic, partitionInfo.partition());
                    // 모든 오프셋 삭제 (토픽의 처음부터 끝까지)
                    recordsToDelete.put(topicPartition, RecordsToDelete.beforeOffset(-1));
                }
            }
            
            // 데이터 삭제 실행
            adminClient.deleteRecords(recordsToDelete).all().get();
        }
    }
}