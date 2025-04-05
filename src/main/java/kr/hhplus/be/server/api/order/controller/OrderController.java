package kr.hhplus.be.server.api.order.controller;

import kr.hhplus.be.server.api.order.dto.OrderRequest;
import kr.hhplus.be.server.api.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController implements OrderSpecification {

    @PostMapping
    public ResponseEntity<OrderResponse> order(@RequestBody OrderRequest request) {

        OrderResponse mock = OrderResponse.builder()
                .orderId(10001L)
                .userId(1001L)
                .productId(10001L)
                .status("PENDING")
                .totalAmount(11000L)
                .discountAmount(1000L)
                .paymentAmount(10000L)
                .build();

        return ResponseEntity.ok(mock);
    }
}
