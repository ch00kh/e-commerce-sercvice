package kr.hhplus.be.server.api.balance.controller;

import kr.hhplus.be.server.api.balance.dto.BalanceRequest;
import kr.hhplus.be.server.api.balance.dto.BalanceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/balance")
public class BalanceController implements BalanceSpecification {

    @PostMapping("/{id}")
    public ResponseEntity<BalanceResponse> charge(
            @PathVariable Long id,
            @RequestBody BalanceRequest request
    ) {

        BalanceResponse mock = BalanceResponse.builder()
                .userId(1001L)
                .amount(10000L)
                .build();

        return ResponseEntity.ok(mock);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BalanceResponse> find(
            @PathVariable Long id
    )  {

        BalanceResponse mock = BalanceResponse.builder()
                .userId(1001L)
                .amount(15000L)
                .build();

        return ResponseEntity.ok(mock);
    }
}
