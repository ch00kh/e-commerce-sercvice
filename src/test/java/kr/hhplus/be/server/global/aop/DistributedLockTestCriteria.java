package kr.hhplus.be.server.global.aop;

import java.util.List;

public record DistributedLockTestCriteria() {
    record SingleLockCriteria(
            Long id
    ) {}

    record MultiLockCriteria(
            List<Item> items
    ) {}

    record Item(
            Long id
    ) {}
}
