package kr.hhplus.be.server.domain.coupon.dto;

import java.util.List;
import java.util.Set;

public record CouponQueueInfo() {

    public record Keys(
            Set<String> couponKeys
    ) {}

    public record UserIds(
            List<Long> userIds
    ) {}
}
