## API 명세서
> ### 📑 목차
> - [잔액 충전 API](#잔액-충전-api)
> - [잔액 조회 API](#잔액-조회-api)
> - [상품 목록 조회 API](#상품-목록-조회-api)
> - [상품 정보 조회 API](#상품-정보-조회-api)
> - [주문 API](#주문-api)
> - [결제 API](#결제-api)
> - [선착순 쿠폰 API](#선착순-쿠폰-api)
> - [인기 판매 상품 조회 API](#인기-판매-상품-조회-api)
--- 

### 잔액 충전 API
- URL : `/api/balance/{user_id}`
- METHOD : `POST`
- Request Body
  ```json
  {
    "amount": 1000
  }
  ```
- Response Header

  | code | message                    | 
  |------|----------------------------|
  | 200  | OK                         |
  | 400  | BAD_REQUEST                |
  | 500  | INTERNATIONAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "code": 200,
    "message": "OK",
    "data": {
      "user_id": 1001,
      "balance": 10000
    }
  }
  ```

--- 
### 잔액 조회 API
- URL : `/api/balance/{user_id}`
- METHOD : `GET`
- Response Header

  | code | message                    |
  |------|----------------------------|
  | 200  | OK                         |
  | 404  | NOT_FOUND                  |
  | 500  | INTERNATIONAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "code": 200,
    "message": "OK",
    "data": {
      "user_id": 1001,
      "balance": 15000
    }
  }
  ```
--- 
### 상품 목록 조회 API
- URL : `/api/product`
- METHOD : `GET`
- Response Header

  | code | message               |
  |------|-----------------------|
  | 200  | OK                    |
  | 500  | INTERNAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "code": 200,
    "message": "OK",
    "data": [
      {
        "product_id": 1001,
        "brand": "총각쓰떡",
        "name": "백설기",
        "options": [
          {
            "detail_id": 101,
            "option_value": "백설기/10개",
            "price": 5500,
            "stock": 100
          },
          {
            "detail_id": 102,
            "option_value": "우유설기/10개",
            "price": 5900,
            "stock": 99
          }
        ]
      },
      {
        "product_id": 1002,
        "brand": "총각쓰떡",
        "name": "백일떡",
        "options": [
          {
            "detail_id": 201,
            "option_value": "백일떡/10개",
            "price": 13700,
            "stock": 200
          }
        ]
      }
    ]
  }
  ```
--- 
### 상품 정보 조회 API
- URL : `/api/product/{product_id}`
- METHOD : `GET`
- Response Header

  | code | message               |
  |------|-----------------------|
  | 200  | OK                    |
  | 404  | NOT_FOUND             |
  | 500  | INTERNAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "code": 200,
    "message": "OK",
    "data": {
      "product_id": 1002,
      "brand": "총각쓰떡",
      "name": "백일떡",
      "options": [
        {
          "detail_id": 201,
          "option_value": "백일떡/10개",
          "price": 13700,
          "stock": 200
        }
      ]
    }
  }
  ```
--- 
### 주문 API
- URL : `/api/order`
- METHOD : `POST`
- Request Body
  ```json
  {
    "user_id": 1001,
    "items": [
      {
        "product_detail_id": 101,
        "quantity": 2
      }
    ],
    "coupon_id": 1001
  }
  ```
- Response Header

  | code | message               |
  |------|-----------------------|
  | 200  | OK                    |
  | 400  | BAD_REQUEST           |
  | 404  | NOT_FOUND             |
  | 500  | INTERNAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "code": 200,
    "message": "OK",
    "data": {
      "order_id": 10001, 
      "user_id": 1001, 
      "status": "PENDING",
      "total_amount": "11000",
      "discount_amount": "1000",
      "payment_amount": "10000"
    }
  }
  ```
--- 
### 결제 API
- URL : `/api/order/payment`
- METHOD : `POST`
- Request Body
  ```json
  {
    "order_id": 10001
  }
  ```
- Response Header

  | code | message               |
  |------|-----------------------|
  | 200  | OK                    |
  | 400  | BAD_REQUEST           |
  | 404  | NOT_FOUND             |
  | 500  | INTERNAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "code": 200,
    "message": "OK"
  }
  ```
--- 
### 선착순 쿠폰 API
- URL : `/api/coupon/{user_id}`
- METHOD : `POST`
- Request Body
  ```json
  {
    "couponId": 1001
  }
  ```
- Response Header

  | code | message               |
  |------|-----------------------|
  | 200  | OK                    |
  | 400  | BAD_REQUEST           |
  | 404  | NOT_FOUND             |
  | 500  | INTERNAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "code": 200,
    "message": "OK"
  }
  ```
--- 
### 인기 판매 상품 조회 API
- URL : `/api/product/best`
- METHOD : `GET`
- Request Body
- Response Header

  | code | message                    |
  |------|----------------------------|
  | 200  | OK                         |
  | 500  | INTERNATIONAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "code": 200,
    "message": "OK",
    "data": [
      {
        "product_id": 1001,
        "brand": "총각쓰떡",
        "name": "백설기",
        "total_orders": 100,
        "option": {
            "detail_id": 101,
            "option_value": "백설기/10개",
            "price": 5500,
            "stock": 100
        }
      },
      {
        "product_id": 1001,
        "brand": "총각쓰떡",
        "name": "백설기",
        "total_orders": 86,
        "option": {
          "detail_id": 102,
          "option_value": "우유설기/10개",
          "price": 5900,
          "stock": 99
        }
      },
      {
        "product_id": 1001,
        "brand": "총각쓰떡",
        "name": "백일떡",
        "total_orders": 32,
        "option": {
          "detail_id": 201,
          "option_value": "백일떡/10개",
          "price": 13700,
          "stock": 92
        }
      }
    ]
  }
  ```