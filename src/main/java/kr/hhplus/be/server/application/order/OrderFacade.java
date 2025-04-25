package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.dto.OrderCriteria;
import kr.hhplus.be.server.application.order.dto.OrderResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponCommand;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentCommand;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderFacade {

    private final ProductService productService;
    private final CouponService couponService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    /**
     * 주문
     */
    @Transactional
    public OrderResult.Create order(OrderCriteria.Create criteria) {

        // 상품 조회
        ProductInfo.ProductAggregate product = productService.findProduct(new ProductCommand.Find(criteria.productId()));

        // 주문 아이템 생성
        List<OrderCommand.OrderItem> orderItemCommand = criteria.items().stream()
                .flatMap(item -> product.options().stream()
                        .filter(option -> item.productOptionId().equals(option.optionId()))
                        .map(option -> new OrderCommand.OrderItem(item.productOptionId(), option.price(), item.quantity())))
                .toList();

        // 주문 생성
        OrderInfo.Create order = orderService.createOrder(new OrderCommand.Create(criteria.userId(), orderItemCommand));

        // 쿠폰 조회, 사용 처리
        CouponInfo.CouponAggregate couponInfo = couponService.use(new CouponCommand.Use(criteria.userId(), criteria.couponId()));

        // 쿠폰 적용
        order = orderService.applyCoupon(OrderCommand.UseCoupon.toCommand(order.orderId(), couponInfo.couponId(), couponInfo.discountPrice()));

        // 재고 차감 -> 재고 부족시 해당 옵션 상태
        ProductInfo.Order checkProductOrder = productService.reduceStock(orderItemCommand);

        // 재고 부족시 -> 생성된 주문아이템 상태 변경(보류)
        OrderInfo.Create finalOrder = order;
        checkProductOrder.checkStocks().forEach(stock -> {
            criteria.items().forEach(criteriaItem -> {
                if (!stock.canPurchase() && criteriaItem.quantity().intValue() != stock.requestQuantity().intValue()) {
                    orderService.holdOrder(new OrderCommand.HoldOrder(finalOrder.orderId(), stock.optionId()));
                }
            });
        });

        //  결제 정보 저장
        paymentService.save(new PaymentCommand.Save(order.orderId(), order.paymentAmount()));

        return OrderResult.Create.from(order);
    }
}
