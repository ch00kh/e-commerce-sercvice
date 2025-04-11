package kr.hhplus.be.server.interfaces.coupon.controller;

import kr.hhplus.be.server.interfaces.coupon.dto.CouponRequest;
import kr.hhplus.be.server.interfaces.coupon.dto.CouponResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupon")
public class CouponController implements ICouponController {

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
