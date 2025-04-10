package kr.hhplus.be.server.domain.balance.entity;

import kr.hhplus.be.server.domain.BaseTimeEntity;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Balance extends BaseTimeEntity {

    private Long id;
    private Long userId;
    private Long balance;

    public void charge(Long amount){
        if (amount < 0) {
            throw new GlobalException(ErrorCode.BAD_REQUEST);
        }
        this.balance += amount;
    }
}
