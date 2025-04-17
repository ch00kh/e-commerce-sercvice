## [요구사항 분석] Sequence Diagram

> ### 📑 목차
> - [잔액 충전 API](#잔액-충전-api)
> - [잔액 조회 API](#잔액-조회-api)
> - [상품 조회 API](#상품-조회-api)
> - [주문 API](#주문-api)
> - [결제 API](#결제-api)
> - [선착순 쿠폰 API](#선착순-쿠폰-api)
> - [인기 판매 상품 조회 API](#인기-판매-상품-조회-api)
---

### 잔액 충전 API
```mermaid
sequenceDiagram
  actor USER
  participant BalanceFacade
  participant BalanceService
  
  activate USER
  USER ->>+ BalanceFacade: 잔액 충전 요청
  BalanceFacade ->>+ BalanceService: 사용자 잔액 충전 요청
  BalanceService ->> BalanceService: 사용자 잔액 조회
  opt 충전 금액이 양수가 아닌 경우
    BalanceService -->> USER: 유효하지 않은 충전 금액 예외 발생(BAD_REQUEST)
  end
  BalanceService ->> BalanceService: 사용자 잔액 충전
  opt 잔고가 최대 금액(10,000,000)을 초과한 경우
    BalanceService -->> USER: 잔고 최대 금액 초과 예외 발생(BAD_REQUEST)
  end
  BalanceService ->>- BalanceFacade: 사용자 잔액 응답
  BalanceFacade ->>- USER: 사용자 잔액 응답
  deactivate USER
```

---

### 잔액 조회 API
```mermaid
sequenceDiagram
  actor USER
  participant BalanceFacade
  participant BalanceService
  
  activate USER
  USER ->>+ BalanceFacade: 잔액 조회 요청
  BalanceFacade ->>+ BalanceService: 사용자 잔액 조회
  BalanceService ->>- BalanceFacade: 사용자 잔액 응답
  BalanceFacade ->>- USER: 조회한 잔액 응답
  deactivate USER
```

---

### 상품 조회 API
```mermaid
sequenceDiagram
  actor USER
  participant Product
  
  activate USER
  USER ->>+ Product: 상품 조회 요청
  alt product_id 가 있는 경우
    Product ->> Product: 상품 정보 조회
    alt 존재하지 않는 상품인 경우
      Product -->> USER: 존재하지 않는 상품인 경우 예외 발생
    end
  else product_id 가 없는 경우
    Product ->> Product: 상품 목록 조회
  end
  Product ->>- USER: 조회한 상품 응답
  deactivate USER
```

---

### 주문 API
```mermaid
sequenceDiagram
  actor USER
  participant OrderFacade
  participant ProductService
  participant OrderService
  participant CouponService

  activate USER
  USER ->>+ OrderFacade: 상품 주문 요청
  OrderFacade ->>+ ProductService: 주문 상품 조회
  ProductService ->>- OrderFacade: 주문 상품 응답
  OrderFacade ->>+ OrderService: 주문 생성
  OrderService->>- OrderFacade: 생성된 주문 응답
  opt 쿠폰 사용하는 경우
    OrderFacade ->>+ CouponService: 쿠폰 사용
    CouponService ->> CouponService: 쿠폰 조회
    opt 유효하지 않은 쿠폰인 경우
      CouponService -->> USER: 유효하지 않은 쿠폰 예외 처리(NOT_FOUND)  
    end
    CouponService ->> CouponService: 쿠폰 사용 처리 
    CouponService ->>- OrderFacade: 쿠폰 적용
    OrderFacade ->>+ OrderService: 생성된 주문에 쿠폰 추가
    OrderService ->>- OrderFacade: 쿠폰 추가된 주문 응답
  end
  OrderFacade ->>+ ProductService: 재고 차감 처리
  ProductService ->> ProductService: 재고 확인 및 차감 
  ProductService ->>- OrderFacade: 상품 옵션별 요청 수량, 재고 응답(충분: true, 부족: false)
  opt 재고 부족시
    OrderFacade ->> OrderService: 부족한 주문 상태 변경
    OrderService ->> OrderService: 주문 상태: PENDING
  end
  OrderFacade ->> PaymentService: 결제 정보 저장
  OrderFacade ->>- USER: 생성된 주문 응답
  deactivate USER
```

---

### 결제 API
```mermaid
sequenceDiagram
  actor USER
  participant PaymentFacade
  participant PaymentService
  participant OrderService
  participant BalanceService
  participant DataPlatform
  
  activate USER
  USER ->>+ PaymentFacade: 결제 요청
  PaymentFacade ->>+ PaymentService: 결제 정보 조회
  PaymentService ->>- PaymentFacade: 결제 정보 응답
  PaymentFacade ->>+ OrderService: 주문 정보 조회
  OrderService ->>- PaymentFacade: 주문 정보 응답
  PaymentFacade ->>+ BalanceService: 결제 금액 차감
  alt 결제금액 부족한 경우
    BalanceService -->> USER: 결제 금액 부족 예외 발생
  end
  BalanceService ->>- PaymentFacade: 사용자 잔액 응답
  PaymentFacade ->>+ OrderService: 주문 상태 변경처리
  OrderService ->> OrderService: 확정 처리
  OrderService ->>- PaymentFacade: 주문 정보 응답 
  PaymentFacade -->> DataPlatform: 주문 정보 전송(비동기 처리)
  PaymentFacade ->>- USER : 사용자 잔액 및 결제 정보 응답
  deactivate USER
```

---

### 선착순 쿠폰 API
```mermaid
sequenceDiagram
  actor USER
  participant CouponFacade
  participant CouponService
  participant Coupon
  participant IssuedCoupon
  
  activate USER
  USER ->>+ CouponFacade: 쿠폰 발급 요청
  CouponFacade ->>+ CouponService: 쿠폰 발급 요청
  CouponService ->>+ Coupon: 잔여 쿠폰 조회
  opt 쿠폰이 소진된 경우
    Coupon -->> USER: 쿠폰 소진 예외 발생()
  end
  Coupon ->>- CouponService: 쿠폰 수량 감소
  CouponService ->>+ IssuedCoupon: 기발급 쿠폰 조회
  opt 이미 발급된 쿠폰인 경우
    IssuedCoupon-->> USER: 기발급된 쿠폰 예외 발생 -> 쿠폰 생성 rollback
  end
  IssuedCoupon ->>- CouponService: 쿠폰 저장
  CouponService ->>- CouponFacade: 쿠폰 발급 응답
  CouponFacade ->>- USER: 쿠폰 발급 응답
  deactivate USER
```

---

### 인기 판매 상품 조회 API
```mermaid
sequenceDiagram
  actor USER
  participant ProductFacade
  participant OrderService
  participant ProductService
  
  activate USER
  USER ->>+ ProductFacade: 인기 판매 상품 조회 요청
  ProductFacade ->>+ OrderService: 최근 X일간 가장 많이 팔린 상위 X개 조회
  OrderService ->>- ProductFacade: 상품옵션id, 판매량 반환 
  ProductFacade ->>+ ProductService: 상품옵션 id기반 상품 정보 조회
  ProductService ->>- ProductFacade: 상품 정보 반환
  ProductFacade ->>- USER: 인기 판매 상품 정보 응답
  deactivate USER
```

