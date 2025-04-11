package kr.hhplus.be.server.interfaces.order.controller;

import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.interfaces.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kr.hhplus.be.server.interfaces.order.dto.OrderRequest.Order;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController implements IOrderController {

    private final OrderFacade orderFacade;

    @PostMapping
    public ResponseEntity<OrderResponse.Create> order(@RequestBody Order request) {

        return ResponseEntity.ok().body(OrderResponse.Create.from(orderFacade.order(request.toCriteria())));
    }
}
