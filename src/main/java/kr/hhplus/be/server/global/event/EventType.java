package kr.hhplus.be.server.global.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    COUPON_APPLY(GroupId.COUPON_SERVICE, Topic.COUPON_APPLY),
    COUPON_USE(GroupId.COUPON_SERVICE, Topic.COUPON_USE),

    ORDER_CREATE(GroupId.ORDER_SERVICE, Topic.ORDER_CREATE),
    ORDER_COMPLETE(GroupId.ORDER_SERVICE, Topic.ORDER_COMPLETE),
    ORDER_STATE(GroupId.ORDER_SERVICE, Topic.ORDER_STATE)
    ;

    private final String groupId;
    private final String topic;

    public static class Topic {
        
        public static final String COUPON_APPLY = "coupon-apply.v1";
        public static final String COUPON_USE = "coupon-use.v1";

        public static final String ORDER_CREATE = "order-create.v1";
        public static final String ORDER_STATE = "order-state.v1";
        public static final String ORDER_COMPLETE = "order-complete.v1";

    }

    public static class GroupId {
        public static final String COUPON_SERVICE = "coupon-service";
        public static final String ORDER_SERVICE = "order-service";
        public static final String PAYMENT_SERVICE = "payment-service";
    }
}
