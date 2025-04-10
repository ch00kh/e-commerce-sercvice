package kr.hhplus.be.server.domain.coupon.entity;

import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseTimeEntity {

    private Long id;
    private Long discountPrice;
    private Integer quantity;
}
