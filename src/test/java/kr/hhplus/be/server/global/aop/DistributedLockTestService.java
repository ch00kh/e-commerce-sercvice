package kr.hhplus.be.server.global.aop;

public class DistributedLockTestService {
    @DistributedLock("test:lock:#{#criteria.id}")
    public String doSomething(DistributedLockTestCriteria.SingleLockCriteria criteria) {
        return "success";
    }

    @DistributedLock("test:lock:#{#criteria.items[*].id}")
    public String doSomething(DistributedLockTestCriteria.MultiLockCriteria criteria) {
        return "success";
    }
}