package kr.hhplus.be.server.api.coupon.controller;

import kr.hhplus.be.server.api.coupon.dto.CouponRequest;
import kr.hhplus.be.server.api.coupon.dto.CouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupon")
public class CouponController implements CouponSpecification {

    @PostMapping("/{id}")
    public ResponseEntity<CouponResponse> issue(
            @PathVariable Long id,
            @RequestBody CouponRequest request
    ) {
        CouponResponse mock = CouponResponse.builder()
                .couponId(1001L)
                .status("ISSUED")
                .build();
        return ResponseEntity.ok(mock);
    }

}
