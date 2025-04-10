package kr.hhplus.be.server.domain.balance.entity;

import kr.hhplus.be.server.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BalanceHistory extends BaseTimeEntity {

    private Long id;
    private Long balanceId;
    private Long issuedCouponId;
    private Long amount;
    private TransactionType transactionType;



}
