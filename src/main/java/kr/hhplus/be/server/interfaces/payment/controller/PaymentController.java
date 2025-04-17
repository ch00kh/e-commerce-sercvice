package kr.hhplus.be.server.interfaces.payment.controller;

import kr.hhplus.be.server.interfaces.payment.dto.PaymentRequest;
import kr.hhplus.be.server.interfaces.payment.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController implements IPaymentController {

    @PostMapping
    public ResponseEntity<PaymentResponse> pay (
            @RequestBody PaymentRequest request
    ) {

        PaymentResponse mock = new PaymentResponse(
                request.orderId(),
                "PAYED"
        );

        return ResponseEntity.ok(mock);
    }
}
