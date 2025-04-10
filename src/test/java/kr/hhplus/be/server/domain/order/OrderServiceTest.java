package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("[단위테스트] OrderService")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private Long USER_ID;
    private Long COUPON_ID;
    private Long ORDER_ID;

    private OrderCommand.OrderItem ORDER_ITEM1;
    private OrderCommand.OrderItem ORDER_ITEM2;
    private List<OrderCommand.OrderItem> ORDER_ITEMS;



    @BeforeEach
    void setup() {
        USER_ID = 1L;
        COUPON_ID = 11L;
        ORDER_ID = 111L;

        ORDER_ITEM1 = new OrderCommand.OrderItem(1L, 10000L, 1);
        ORDER_ITEM2 = new OrderCommand.OrderItem(2L, 5000L, 2);
        ORDER_ITEMS = List.of(ORDER_ITEM1, ORDER_ITEM2);
    }

    @Test
    @DisplayName("[성공] 주문 생성")
    void createOrder_hasNoCoupon_ok() {

        // Arrange
        Order order = Order.builder()
                .userId(USER_ID)
                .issuedCouponId(null)
                .totalAmount(20000L)
                .build();

        OrderCommand.Create command = OrderCommand.Create.builder()
                .userId(USER_ID)
                .issuedCouponId(null)
                .orderItems(ORDER_ITEMS)
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Act
        OrderInfo.Create actualInfo = orderService.createOrder(command);

        // Assert
        verify(orderRepository, times(1)).save(any(Order.class));

        assertThat(actualInfo.userId()).isEqualTo(USER_ID);
        assertThat(actualInfo.status()).isEqualTo(OrderStatus.CREATED);
        assertThat(actualInfo.totalAmount()).isEqualTo(20000L);
        assertThat(actualInfo.discountAmount()).isEqualTo(0L);
        assertThat(actualInfo.paymentAmount()).isEqualTo(20000L);

    }

    @Test
    @DisplayName("[성공] 주문 후 상품 상태 변경 (CREATE -> PENDING)")
    void holdOrder() {

        // Arrange
        Long productOptionId = 1L;
        OrderCommand.HoldOrder command = new OrderCommand.HoldOrder(productOptionId);

        OrderItem orderItem = OrderItem.builder()
                .orderId(1L)
                .productOptionId(1L)
                .unitPrice(1000L)
                .quantity(100)
                .build();

        when(orderItemRepository.findByProductOptionId(productOptionId)).thenReturn(Optional.of(orderItem));

        // Act
        orderService.holdOrder(command);

        // Assert
        verify(orderItemRepository, times(1)).findByProductOptionId(productOptionId);
        assertEquals(OrderStatus.PENDING, orderItem.getStatus());
    }

    @Nested
    @DisplayName("주문 생성 후 쿠폰 적용")
    class useCoupon {

        @Test
        @DisplayName("[성공] 쿠폰 적용 시 금액 계산 (주문금액 > 할인금액)")
        void useCoupon_totalAmountGtDiscountAmount() {

            // Arrange
            Order order = Order.builder()
                    .userId(USER_ID)
                    .issuedCouponId(null)
                    .totalAmount(10000L)
                    .build();

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 3000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            // when
            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            // then
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(3000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(7000L);
        }

        @Test
        @DisplayName("[성공] 쿠폰 적용 시 금액 계산 (주문금액 < 할인금액)")
        void useCoupon_totalAmountLtDiscountAmount() {

            // Arrange
            Order order = Order.builder()
                    .userId(USER_ID)
                    .issuedCouponId(null)
                    .totalAmount(10000L)
                    .build();

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 20000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            // when
            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            // then
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(10000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("[성공] 쿠폰 적용 시 금액 계산 (주문금액 < 할인금액)")
        void useCoupon_totalAmountEqDiscountAmount() {

            // Arrange
            Order order = Order.builder()
                    .userId(USER_ID)
                    .issuedCouponId(null)
                    .totalAmount(10000L)
                    .build();

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 10000L);

            when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

            // when
            OrderInfo.Create actualInfo = orderService.useCoupon(command);

            // then
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(10000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(0L);
        }
    }
}