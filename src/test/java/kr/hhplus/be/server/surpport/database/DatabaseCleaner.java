package kr.hhplus.be.server.surpport.database;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager em;
    
    /**
     * 테이블명 저장
     */
    private final List<String> tables = new ArrayList<>();
    private boolean isInitialized = false;

    /**
     * 테이블 조회
     */
    public void findTableNames() {
        tables.clear();

        List<String> tableNames = em.createNativeQuery("SHOW TABLES").getResultList();

        for (String tableName : tableNames) {
            tables.add(tableName);
        }

        isInitialized = true;
    }

    /**
     * FK 제약 조건 임시 해제 후 테이블 데이터 초기화
     */
    private void truncate() {
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        for (String tableName : tables) {
            em.createNativeQuery(String.format("TRUNCATE TABLE `%s`", tableName)).executeUpdate();
        }

        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }

    /**
     * 영속성 컨텍스트 1차 캐시 제거 및 데이터 초기화
     */
    @Transactional
    public void clear() {

        if (!isInitialized) {
            findTableNames();
        }

        em.clear();
        truncate();
    }
}