# 동시성 이슈 해결을 위한 DB Lock 적용 보고서

> ### 📑 목차
> - [문제식별](#문제-식별)
> - [분석](#분석)
>   - [Pessimistic Lock (비관적 락)](#pessimistic-lock-비관적-락)
>   - [Optimistic Lock (낙관적 락)](#optimistic-lock-낙관적-락)
>   - [Lock 전략 비교](#lock-전략-비교)
> - [해결](#해결)
>   - [사용자 잔액 충전](#1-사용자-잔액-충전)
>   - [상품 주문 시 재고 차감](#2-상품-주문-시-재고-차감)
>   - [결제 시 결제 정보 변경](#3-결제-시-결제-정보-변경)
>   - [결제 시 사용자 잔고 차감](#4-결제-시-사용자-잔액-차감)
>   - [선착순 쿠폰 발급](#5-선착순-쿠폰-발급)
> - [대안]()
>   - [Named Lock (Mysql 분산 락)](#named-lock-mysql-분산-락)
>   - [분산 락(redis)](#분산-락redis) 
>   - [메시지 큐(kafka)](#메시지-큐kafka)

---

## 문제 식별

E-COMMERCE 시나리오에서 동시성 이슈가 발생할 가능성이 있는 서비스 로직은 아래와 같다.

1. **사용자 잔액 충전**
2. **상품 주문 시 재고 차감**
3. **결제 시 결제 정보 변경**
4. **결제 시 사용자 잔액 차감**
5. **선착순 쿠폰 발급**

동시성 이슈가 발생한 경우 데이터 정합성 문제가 발생하여, 비즈니스 로직 오류, 사용자 경험 저하, 매출 손실 등의 결과를 초래할 수 있다.

---

## 분석

### Pessimistic Lock (비관적 락)

- 충돌이 발생한다고 **비관적으로 가정하는 방법**
- `Repeatable Read`, `Serializable` 정도의 격리성에서 가능하다.
- DB에서 제공하는 Lock을 사용한다.
- 데이터에 잠금을 걸어서 잠금이 해제될때까지 다른 트랜잭션의 데이터 조회를 제한한다.
- 앞의 트랜잭션이 끝날때 까지 기다렸다가 작업을 수행함으로써 데이터 정합성이 보장된다.
- 대기시간이  DB의 리소스 부하가 증가되면서 성능이 저하될 수 있으며 타임아웃이 발생할 수 도 있다.
- 데이터를 수정하는 즉시 트랜잭션 충돌을 감지 할 수 있다.
- 데드락이 걸릴 수 있으므로 주의하여 사용하여야 합니다.

### Optimistic Lock (낙관적 락)

- 충돌이 발생하지 않아 동시성 문제가 발생하지 않는다고 **낙관적으로 가정하는 방법**
- DB에서 Lock을 거는 것이 아닌 Application Level 에서 제공하는 Versioning 을 사용한다.
- Versioning을 통해 갱신손실을 방지하고, 데이터 정합성을 맞춘다. 
- 실제로 DB Lock을 사용하는 것이 아니므로 성능적인 이점이 있다.
- 동시성 문제가 발생하여 작업 반영에 실패하는 경우 정책에 맞는 로직을 추가로 작성하면서 복작성이 증가할 수 있다.
- 트랜잭션을 커밋하는 시점에 충돌을 알 수 있다.

### Lock 전략 비교

|  비교 항목   | Pessimistic Lock (비관적 락)                       | Optimistic Lock (낙관적 락)             |
|:--------:|:-----------------------------------------------|:------------------------------------|
|   락 방식   | DB에서 테이블/로우에 락                                 | DB에서 락을 걸지 않고 버전 정보 활용              |
| 성능 및 영향  | 대기시간이 길수록 성능 저하                                | DB리소스 점유가 적어 성능 우수                  |
| 충돌 감지 시점 | 데이터 수정 시점                                      | 트랜잭션 커밋 시점                          |
|    장점    | 작업을 반드시 수행                                     | 높은 처리량과 성능 우수                       |
|    단점    | 대기시간이 길수록 성능 저하<br/>타임아웃 발생 가능성 증가<br/>데드락 가능성 | 충돌 시 정책에 따른 재시도 로직 필요<br/>구현복잡도 증가  |
|  적합한 상황  | 충돌이 자주 발생하여 하는 상황<br/> 작업을 반드시 수행해야하는 상황       | 충돌이 적은 상황<br/> 처리량이 많아 성능을 중요시하는 상황 |
| 트랜잭션 종료시 | 락 자동 해제                                        | 버전 업데이트로 처리                         |

---

## 해결

### 1. 사용자 잔액 충전
- 사용자가 자신의 계정에서만 작업하므로 동시 충전 빈도가 낮을 것으로 예상된다.
- 만약 동시성 이슈 발생한다면, 사용자의 실수로 중복 요청으로 판단된다.
- 사용자의 실수로 중복 요청이 된 경우에 첫번째 이후의 요청은 실패 처리가 되면 사용자에게 실패를 반환한다.
- 따라서 성능적 이점을 챙길 수 있는 **낙관적 락**이 적합하다.

<details>
<summary><b>AS-IS</b></summary>

```java
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);

}
```
</details>
<details>
<summary><b>TO-BE : 낙관적 락 적용</b></summary>

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {@Index(name = "idx_user_id", columnList = "userId")})
public class Balance extends BaseTimeEntity {

    // ...

    @Version
    private Long version; // 버전 추가
}
```
```java
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b From Balance b WHERE b.userId = :userId")
    Optional<Balance> findByUserIdWithOptimisticLock(Long userId);

}
```
</details>

### 2. 상품 주문 시 재고 차감
- 인기 상품의 경우 동시에 많은 구매 요청 발생 가능하다.
- 재고는 정확히 관리되어야 하는 중요한 데이터로 판단되며 처리량 보다 데이터 정합성이 우선시 되어야 한다고 판단된다. 
- 또한, 트래픽이 집중되는 경우에 낙관적 락을 사용하면 충돌이 많아 재시도 로직이 복잡해질 것으로 예상된다.
- 따라서 정확한 데이터를 관리해야하면서 구현 복잡도가 낮은 **비관적 락**이 적합하다.

<details>
<summary><b>AS-IS</b></summary>

```java
public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProductId(Long productId);

}
```
</details>
<details>
<summary><b>TO-BE : 비관적 락 적용</b></summary>

```java
public interface ProductOptionJpaRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProductId(Long productId);
    
    // 비관적 락 추가
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o From ProductOption o WHERE o.id = :optionId")
    Optional<ProductOption> findByIdWithPessimisticLock(Long optionId);
}
```

</details>

### 3. 결제 시 결제 정보 변경
- 일반적으로 한 주문에 대해서 동시에 결제 요청이 들어온 경우는 거의 없을 것이라고 판단된다.
- 만약 결제 요청이 동시에 여러번 들어오는 경우는 시스템의 취약점을 악용한 사례로 판단할 수 있다.
- 따라서 동시성 이슈는 거의 없을 것으로 예상되며 충돌 가능성이 낮은 **낙관적 락**이 적합하다.

<details>
<summary><b>AS-IS</b></summary>

```java
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);
    
}
```
</details>
<details>
<summary><b>TO-BE : 낙관적 락 적용</b></summary>

```java
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = @Index(name = "idx_order_id", columnList = "orderId"))
public class Payment extends BaseTimeEntity {

    //...

    @Version
    private Long version;  // 버전 추가
}

```
```java
public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    // 낙관적 락 추가
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p From Payment p WHERE p.id = :paymentId")
    Optional<Payment> findByIdWithOptimisticLock(Long paymentId);
}
```

</details>

### 4. 결제 시 사용자 잔액 차감
- 결제는 절대 중복되거나 누락되어서는 안 되는 중요 트랜잭션이다.
- 데이터 정합성이 성능보다 우선시 되어야 한다.
- 결제 실패 시 사용자 경험이 크게 저하될 수 있다.
- 따라서 **비관적 락**이 적합하다.

<details>
<summary><b>AS-IS</b></summary>

```java
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b From Balance b WHERE b.userId = :userId")
    Optional<Balance> findByUserIdWithOptimisticLock(Long userId);
    
}
```
</details>
<details>
<summary><b>TO-BE : 비관적 락 적용</b></summary>

```java
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {

    Optional<Balance> findByUserId(Long userId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT b From Balance b WHERE b.userId = :userId")
    Optional<Balance> findByUserIdWithOptimisticLock(Long userId);

    // 비관적 락 추가
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b From Balance b WHERE b.userId = :userId")
    Optional<Balance> findByUserIdWithPessimisticLock(Long userId);
}
```

</details>

### 5. 선착순 쿠폰 발급
- 특정 프로모션에 쿠폰 발급에 대한 트래픽이 집중될 수 있다.
- 트래픽 집중 정도에 따라 쿠폰 발급시 대기 시간(사용자 경험 저하)과 처리량을 고려해볼 수 있다.
- 쿠폰 수량 제한이 명확하고 정확히해야 좋지만, 트래릭이 많이 집중되는 경우 서버의 부담의 증가하게 되어 대기 시간 증가로 인해 사용자 경험을 저하시킬 수 있다.
- 또한, 일반적으로 이벤트는 당첨이 되면 좋지만, 당첨이 안되더라도 크게 아쉬워하지 않는다.
- 따라서 **낙관적 락**이 적합하다.
- 추가로, 서버가 여러 대인 경우 분산 환경에서의 동시성 제어 필요하다. 이런 경우 Redis를 도입하여 분산 락을 사용하여 확장성을 높일 수 있다.


<details>
<summary><b>AS-IS</b></summary>

```java
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {
}
```
</details>
<details>
<summary><b>TO-BE : 낙관적 락 적용</b></summary>

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Coupon extends BaseTimeEntity {

    //...
    
    @Version
    Long version; // 버전 추가
    
}
```
```java
public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

    // 낙관적 락 추가
    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT c From Coupon c WHERE c.id = :couponId")
    Coupon findByIdWithOptimisticLock(Long couponId);
}
```
</details>

---

## 대안

### Named Lock (Mysql 분산 락)
- 이름을 가진 Metadata Locking 이다.
- 이름을 가진 Lock을 획득한 후 해제할때까지 다른 세션은 이 Lock을 획득할 수 없도록 한다.
- 트랜잭션이 종료될 때 Lock이 자동으로 해제되지 않으므로 Lock을 명시적으로 해제해야한다.
- 별도의 트랜잭션으로 실행해야 하므로 전파속성을 변경해야한다.
- 비관적 락과 유사하지만, 비관적 락은 table, row 단위로 락을 걸지만, 네임드락은 metadata에 락을 건다.
- 선착순 이벤트, 중복 처리 방지 등의 시나리오에 적합하다.

### 분산 락(redis)
- Redis의 원자적 명령어를 활용한 분산 환경에서의 락 구현 방식이다.
- 단일 Redis 서버나 클러스터를 통해 여러 애플리케이션 서버 간의 동시성을 제어한다.
- Redis는 인메모리 기반으로 디스크 IO를 사용하는 DB락 보다 성능이 우수하다.
- 만료 시간(TTL)을 설정하여 데드락 방지가 가능하다.
- 락의 획득과 해제가 실패할 경우를 대비한 전략이 필요하다.
- 높은 처리량이 필요한 동시성 제어, 글로벌 락이 필요한 시나리오에 적합하다.
 
### 메시지 큐(kafka)
- 비동기 처리를 통해 동시성 이슈를 회피하는 방식이다.
- 동시에 들어온 요청을 큐에 저장하고 순차적으로 처리한다.
- 시스템 부하 분산 및 장애 내구성이 뛰어나다.
- 데이터 손실 위험을 줄이고 안정적인 처리가 가능하다.
- 재시도 메커니즘을 통해 실패한 작업을 안전하게 처리할 수 있다.
- 대규모 트래픽 처리, 시스템 간 결합도 감소, 피크 타임 부하 관리에 적합하다.