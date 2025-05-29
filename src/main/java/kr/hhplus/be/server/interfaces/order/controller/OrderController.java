package kr.hhplus.be.server.interfaces.order.controller;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.interfaces.order.dto.OrderRequest;
import kr.hhplus.be.server.interfaces.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements IOrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse.Create> order(
            @RequestBody OrderRequest.Create request
    ) {
        return ResponseEntity.ok().body(OrderResponse.Create.from(orderService.createOrder(request.toCommand())));
    }
}
