package kr.hhplus.be.server.interfaces.coupon.controller;

import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponRequest;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupon")
@RequiredArgsConstructor
public class CouponController implements ICouponController {

    private final CouponFacade couponFacade;

    @PostMapping("/issue")
    public ResponseEntity<CouponResponse> issue(@RequestBody CouponRequest request) {
        return ResponseEntity.ok()
                .body(CouponResponse.from(couponFacade.firstComeFirstIssue(request.toCriteria())));
    }
}
