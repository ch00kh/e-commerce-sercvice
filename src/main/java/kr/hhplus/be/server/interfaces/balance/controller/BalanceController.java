package kr.hhplus.be.server.interfaces.balance.controller;

import kr.hhplus.be.server.application.balance.BalanceFacade;
import kr.hhplus.be.server.interfaces.balance.dto.BalanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static kr.hhplus.be.server.interfaces.balance.dto.BalanceResponse.UserBalance;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController implements IBalanceController {

    private final BalanceFacade balanceFacade;

    @PostMapping("/{userId}")
    public ResponseEntity<UserBalance> charge(
            @PathVariable Long userId,
            @Validated @RequestBody BalanceRequest.Charge request
    ) {
        return ResponseEntity.ok()
                .body(UserBalance.from(balanceFacade.charge(request.toCriteria(userId))));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserBalance> findBalance(@PathVariable Long userId) {

        return ResponseEntity.ok()
                .body(UserBalance.from(balanceFacade.findBalance(BalanceRequest.Find.toCriteria(userId))));
    }
}
