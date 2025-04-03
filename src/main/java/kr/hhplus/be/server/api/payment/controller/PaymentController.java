package kr.hhplus.be.server.api.payment.controller;

import kr.hhplus.be.server.api.payment.dto.PaymentRequest;
import kr.hhplus.be.server.api.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController implements PaymentSpecification {

    @PostMapping
    public ResponseEntity<PaymentResponse> pay (
            @RequestBody PaymentRequest request
    ) {

        PaymentResponse mock = new PaymentResponse(
                request.orderId(),
                "COMPLETE"
        );

        return ResponseEntity.ok(mock);
    }
}
