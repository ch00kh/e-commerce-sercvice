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
- URL : `/api/balance/{id}`
- METHOD : `POST`
- Request Body
  ```json
  {
    "amount": 1000
  }
  ```
- Response Header

  | code | message               | 
  |------|-----------------------|
  | 200  | OK                    |
  | 400  | BAD_REQUEST           |
  | 500  | INTERNAL_SERVER_ERROR |
- Response Body
  ```json
  {
    "user_id": 1001,
    "balance": 10000
  }
  ```

--- 
### 잔액 조회 API
- URL : `/api/balance/{id}`
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
      "userId": 1001,
      "amount": 10000
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
  [
      {
          "productId": 1002,
          "brand": "총각쓰떡",
          "name": "백설기",
          "options": [
              {
                  "productDetailId": 101,
                  "optionValue": "백설기/10개",
                  "price": 5500,
                  "stock": 100
              },
              {
                  "productDetailId": 102,
                  "optionValue": "우유설기/10개",
                  "price": 5900,
                  "stock": 99
              }
          ]
      },
      {
          "productId": 1002,
          "brand": "총각쓰떡",
          "name": "백일떡",
          "options": [
              {
                  "productDetailId": 201,
                  "optionValue": "백일떡/10개",
                  "price": 13700,
                  "stock": 200
              }
          ]
      }
  ]
  ```
--- 
### 상품 정보 조회 API
- URL : `/api/product/{id}`
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
      "productId": 1002,
      "brand": "총각쓰떡",
      "name": "백일떡",
      "options": [
          {
              "productDetailId": 201,
              "optionValue": "백일떡/10개",
              "price": 13700,
              "stock": 200
          }
      ]
  }
  ```
--- 
### 주문 API
- URL : `/api/order`
- METHOD : `POST`
- Request Body
  ```json
  {
    "userId": 1001,
    "productId": 1001,
    "items": [
      {
        "product_option_id": 101,
        "quantity": 2
      }
    ],
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
    "orderId": 10001,
    "userId": 1001,
    "productId": 10001,
    "status": "PENDING",
    "totalAmount": 11000,
    "discountAmount": 1000,
    "paymentAmount": 10000
  }
  ```
--- 
### 결제 API
- URL : `/api/payment`
- METHOD : `POST`
- Request Body
  ```json
  {
    "userId": 1001,
    "orderId": 10001
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
    "orderId": 1001,
    "status": "COMPLETE"
  }
  ```
--- 
### 선착순 쿠폰 API
- URL : `/api/coupon/{id}`
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
  | 404  | NOT_FOUND             |
  | 500  | INTERNAL_SERVER_ERROR |

--- 
### 인기 판매 상품 조회 API
- URL : `/api/product/best`
- METHOD : `GET`
- Request Body
- Response Header

  | code | message               |
  |------|-----------------------|
  | 200  | OK                    |
  | 500  | INTERNAL_SERVER_ERROR |
- Response Body
  ```json
  [
    {
      "productId": 1001,
      "brand": "총각쓰떡",
      "name": "백설기",
      "totalOrders": 100,
      "option": {
        "detailId": 101,
        "optionValue": "백설기/10개",
        "price": 5500,
        "stock": 100
      }
    },
    {
      "productId": 1001,
      "brand": "총각쓰떡",
      "name": "백설기",
      "totalOrders": 86,
      "option": {
        "detailId": 102,
        "optionValue": "우유설기/10개",
        "price": 5900,
        "stock": 99
      }
    },
    {
      "productId": 1001,
      "brand": "총각쓰떡",
      "name": "백일떡",
      "totalOrders": 32,
      "option": {
        "detailId": 201,
        "optionValue": "백일떡/10개",
        "price": 13700,
        "stock": 92
      }
    }
  ]
  ```
