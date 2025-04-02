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
  participant Balance
  
  activate USER
  USER ->>+ Balance: 잔액 충전 요청
  opt 충전 금액이 양수가 아닌 경우
    Balance -->> USER: 유효하지 않은 충전 금액 예외 발생
  end
  Balance ->> Balance: 잔고 조회, 충전
  opt 잔고가 최대 금액을 초과한 경우
    Balance -->> USER: 잔고 최대 금액 초과 예외 발생
  end
  Balance ->> Balance: 잔액 저장
  Balance -->>- USER: 사용자 잔액 응답
  deactivate USER
```

---

### 잔액 조회 API
```mermaid
sequenceDiagram
  actor USER
  participant Balance
  
  activate USER
  USER ->>+ Balance: 잔액 조회 요청
  Balance ->> Balance: 잔액 조회
  Balance ->>- USER: 조회한 잔액 응답
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
  participant Order
  participant Product
  participant Coupon
  participant Payment

  activate USER
  USER ->>+ Order: 상품 주문 요청
  Order ->>+ Product: 상품 정보 조회
  deactivate Order
  Product -->>- Order: 상품 정보 반환
  activate Order
  opt 쿠폰을 사용하는 경우
    Order ->>+ Coupon: 유효 쿠폰 검증 요청
    deactivate Order
    Coupon ->> Coupon: 쿠폰 검증
    opt 유효하지 않은 쿠폰인 경우
      Coupon -->> USER: 유효하지 않은 쿠폰 예외 발생
    end
    Coupon -->>- Order: 쿠폰 정보 반환
    activate Order
    Order ->> Order: 쿠폰 할인 적용
  end
  
  Order ->> Order: 주문 정보 생성

  opt 재고 부족한 경우
    Order ->> Order: 주문 실패 업데이트
    Order -->> USER: 재고 부족 예외 발생
  end
  Order ->>+ Product: 재고 차감 처리
  Product -->>- Order: 재고 차감 완료 
  opt 쿠폰을 사용하는 경우
    Order ->>+ Coupon: 쿠폰 사용 요청
    Coupon -->>- Order: 쿠폰 사용 처리
  end
  Order ->>+ Payment: 결제 정보 저장
  Payment -->>- Order: 결제 정보 반환
  
  Order -->>- USER: 주문 생성 정보 응답
  deactivate USER
```

---

### 결제 API
```mermaid
sequenceDiagram
  actor USER
  participant Payment
  participant Order
  participant Balance
  participant DataPlatform
  USER ->>+ Payment: 결제 요청
  Payment ->>+ Order: 주문 정보 조회
  Order -->>- Payment: 주문 정보 반환
  Payment ->>+ Balance: 포인트 차감 요청
  Balance ->> Balance: 포인트 조회
  deactivate Payment
  alt 포인트가 부족한 경우
    Balance -->> USER: 포인트 부족 예외 발생
  end
  activate Payment
  Balance ->> Balance: 포인트 차감
  Balance -->>- Payment: 포인트 정보 반환
  Payment ->> Payment: 결제 상태 완료 처리
  Payment ->>+ Order: 주문 상태 확정 처리
  Order -->>- Payment: 주문 상태 확정 완료
  Payment -->> DataPlatform: 주문 정보 전송(비동기 처리)
  Payment -->>- USER: 결제 완료 응답
```

---

### 선착순 쿠폰 API
```mermaid
sequenceDiagram
  actor USER
  participant Coupon
  participant Issued_Coupon
  
  activate USER
  USER ->>+ Coupon: 쿠폰 발급 요청
  Coupon ->> Coupon: 잔여 쿠폰 조회
  opt 쿠폰이 소진된 경우
    Coupon -->> USER: 쿠폰 소진 예외 발생
  end
  Coupon ->> Coupon: 쿠폰 생성
  Coupon ->>+ Issued_Coupon: 쿠폰 저장
  Issued_Coupon ->> Issued_Coupon: 기발급 쿠폰 조회
  opt 이미 발급된 쿠폰인 경우
    Issued_Coupon ->> Coupon: 쿠폰 생성 취소
    Coupon ->> USER: 기발급된 쿠폰 예외 발생
  end
  Issued_Coupon ->>- Coupon: 쿠폰 저장 완료
  Coupon -->- USER: 쿠폰발급 완료 응답
  deactivate USER
```

---

### 인기 판매 상품 조회 API
```mermaid
sequenceDiagram
  actor USER
  participant Product
  participant OrderProduct
  
  activate USER
  USER ->>+ Product: 인기 판매 상품 조회 요청
  Product ->>+ OrderProduct: 최근 3일간 가장 많이 팔린 상위 5개 조회
  OrderProduct -->>- Product: 인기 판매 상품 반환
  Product ->> Product: 인기 판매 상품 기반 상품 정보 조회
  Product -->>- USER: 인기 판매 상품 정보 응답
  deactivate USER
```

