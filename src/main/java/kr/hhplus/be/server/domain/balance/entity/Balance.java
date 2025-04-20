package kr.hhplus.be.server.domain.balance.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {@Index(name = "idx_user_id", columnList = "userId")})
public class Balance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long balance;

    public Balance(Long userId, Long balance) {
        this.userId = userId;
        this.balance = balance;
    }

    public Balance charge(Long amount){
        if (amount < 0) {
            throw new GlobalException(ErrorCode.INVALID_CHARGE_AMOUNT);
        }

        this.balance += amount;

        if (this.balance > 10000000L) {
            throw new GlobalException(ErrorCode.BALANCE_EXCEED_MAXIMUM);
        }

        return this;
    }

    public Balance reduce(Long amount) {
        if (this.balance < amount) {
            throw new GlobalException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        this.balance -= amount;

        return this;
    }
}
