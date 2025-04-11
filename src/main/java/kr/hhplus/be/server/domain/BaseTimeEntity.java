package kr.hhplus.be.server.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseTimeEntity {

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
