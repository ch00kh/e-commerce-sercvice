package kr.hhplus.be.server.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseCreatedTimeEntity {

    private LocalDateTime createdAt;

}
