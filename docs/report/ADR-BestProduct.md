# 인기 판매 상품 조회 쿼리 성능 개선

## 상태 
- 채택.

## 배경
- 인기 판매 상품 조회 시 API의 성능이 저하되는 문제가 발생할 것으로 예상됩니다.
- 왜냐하면 해당 쿼리는 최근 3일간 가장 많이 판매된 상품 옵션을 조회하는 기능을 수행하는데 있어 시간이 지날수록 주문이 점점 쌓여감에 따라 조건절의 조회속도 더뎌질 것으로 예상됩니다.
- 해당 보고서는 상품 10만 건과 2백만 건의 주문 데이터를 넣어서 테스트한 결과입니다.

예상 병목지점 코드
```java
@Query("SELECT oi.productOptionId as productOptionId, SUM(oi.quantity) as totalSaleQuantity " +
       "FROM OrderItem oi " +
       "WHERE oi.createdAt >= :startDate " +
       "GROUP BY oi.productOptionId " +
       "ORDER BY SUM(oi.quantity) DESC")
List<BestSellingProjection> findBestSelling(@Param("startDate") LocalDateTime startDate, Pageable pageable);

interface BestSellingProjection {
    Long getProductOptionId();
    Long getTotalSaleQuantity();
}
```

실제 실행되는 SQL
```sql
SELECT
    oi.product_option_id AS productOptionId,
    SUM(oi.quantity) AS totalSaleQuantity
FROM order_item oi
WHERE oi.created_at >= DATE_SUB(NOW(), INTERVAL 3 DAY)
GROUP BY oi.product_option_id
ORDER BY SUM(oi.quantity) DESC
LIMIT 5;
```

## 결정
해당쿼리는 PK를 통한 조회가 아니므로 성능 개선 대상 쿼리로 판단됩니다.  
테이블의 전체 스캔에서 발생하는 성능 병목을 해결하기 위해 **커버링 인덱스를 적용**합니다.

```sql
CREATE INDEX idx_order_item_created_prod_opt
ON order_item(created_at, product_option_id, quantity);
```

### 기존 쿼리 실행 계획 분석
```
-> Limit: 5 row(s)  (actual time=3772..3772 rows=5 loops=1)
    -> Sort: `sum(quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=3772..3772 rows=5 loops=1)
        -> Table scan on <temporary>  (actual time=3752..3760 rows=100000 loops=1)
            -> Aggregate using temporary table  (actual time=3752..3752 rows=100000 loops=1)
                -> Filter: (order_item.created_at >= <cache>((now() - interval 3 day)))  (cost=75991 rows=663285) (actual time=7.09..2397 rows=2e+6 loops=1)
                    -> Table scan on order_item  (cost=75991 rows=1.99e+6) (actual time=7.07..2258 rows=2e+6 loops=1)
```
- `order_item` 테이블의 전체 스캔이 발생하여 성능 개선의 여지가 있음을 확인됩니다.

### 카디널리티 분석
```sql
SELECT COUNT(DISTINCT product_option_id) AS product_option_cardinality,
       COUNT(DISTINCT created_at) AS created_at_cardinality,
       COUNT(*) AS total_rows
FROM order_item;
```
| product_option_cardinality | created_at_cardinality | total_rows |
|----------------------------|------------------------|------------|
| 100000                     | 20                     | 2000000    |

- `product_option_id`: 100,000건의 고유값 (높은 카디널리티)
- `created_at`: 20건의 고유값 (낮은 카디널리티)
- `total_rows`: 2,000,000건

### 인덱스 설계 원칙 적용
1. WHERE 절에 사용되는 컬럼 (`created_at`)
2. GROUP BY 절에 사용되는 컬럼 (`product_option_id`)
3. SELECT 절에서 사용되는 컬럼(집계 함수) (`quantity`)

## 결과
### 커버링 인덱스 적용 후 실행 계획 분석
```
-> Limit: 5 row(s)  (actual time=1446..1446 rows=5 loops=1)
    -> Sort: `sum(quantity)` DESC, limit input to 5 row(s) per chunk  (actual time=1446..1446 rows=5 loops=1)
        -> Table scan on <temporary>  (actual time=1428..1435 rows=100000 loops=1)
            -> Aggregate using temporary table  (actual time=1428..1428 rows=100000 loops=1)
                -> Filter: (order_item.created_at >= <cache>((now() - interval 3 day)))  (cost=203002 rows=995027) (actual time=0.341..541 rows=2e+6 loops=1)
                    -> Covering index range scan on order_item using idx_covering_all over ('2025-04-14 19:57:59.000000' <= created_at)  (cost=203002 rows=995027) (actual time=0.336..411 rows=2e+6 loops=1)
```
### 성능 개선 결과

|               |      인덱스 X      |      커버링 인덱스       |         비교         |
|:-------------:|:---------------:|:------------------:|:------------------:|
|     접근 방식     |  all (테이블 스캔)   | range (인덱스 범위 스캔)  |  풀 스캔 → 인덱스 범위 스캔  |
|    필터링 시간     |  7.09ms~2397ms  |  0.341ms ~ 541ms   |     4.4배 성능 개선     |
|   전체 쿼리 시간    |     3772ms      |       1446ms       |    약 2.6배 성능 개선    |


## 영향
1. **긍정적 영향**
  - 인기 판매 상품 조회 응답 시간 2.6배 단축
  - 데이터베이스 부하 감소
  - 사용자 경험 향상

2. **부정적 영향**
  - 인덱스 추가로 인한 디스크 공간 사용량 증가
  - INSERT/UPDATE 연산 시 오버헤드 발생 가능
  - 인덱스 유지보수 필요

## 대안
1. **Materialized View 생성**
- 주기적으로 갱신되는 집계 테이블 구현
- 구현의 복잡도 및 추가 관리 필요성 고려