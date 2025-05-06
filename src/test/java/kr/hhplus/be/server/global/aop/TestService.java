package kr.hhplus.be.server.global.aop;

public class TestService {
    @DistributedLock("test:lock:#{#criteria.id}")
    public String doSomething(TestCriteria.SingleLockCriteria criteria) {
        return "success";
    }

    @DistributedLock("test:lock:#{#criteria.items[*].id}")
    public String doSomething(TestCriteria.MultiLockCriteria criteria) {
        return "success";
    }
}
