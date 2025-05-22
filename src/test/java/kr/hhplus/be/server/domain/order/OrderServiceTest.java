package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.order.event.OrderEvent;
import kr.hhplus.be.server.domain.order.event.OrderEventPublisher;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private OrderEventPublisher eventPublish;

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

        ORDER_ITEM1 = new OrderCommand.OrderItem(1L, 10000L, 1L);
        ORDER_ITEM2 = new OrderCommand.OrderItem(2L, 5000L, 2L);
        ORDER_ITEMS = List.of(ORDER_ITEM1, ORDER_ITEM2);
    }

    @Test
    @DisplayName("사용자ID와 주문아이템으로 주문을 생성한다.")
    void createOrder_hasNoCoupon_ok() {

        // Arrange
        Order order = new Order(USER_ID, 20000L);

        OrderCommand.Create command = new OrderCommand.Create(USER_ID, ORDER_ITEMS);

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
    @DisplayName("주문 후 재고가 부족한 경우 주문아이템의 상태가 변경(PENDING)된다.")
    void holdOrders() {

        // Arrange
        Long productOptionId = 1L;
        OrderItem orderItem = new OrderItem(1L, 1L, 1000L, 101L);
        List<ProductInfo.OptionDetail> optionDetails = List.of(new ProductInfo.OptionDetail(1L, false, 101L, 100L));

        OrderCommand.handleOrders command = new OrderCommand.handleOrders(1L, optionDetails);

        when(orderItemRepository.findByOrderIdAndProductOptionId(1L, productOptionId)).thenReturn(orderItem);

        // Act
        orderService.holdOrders(command);

        // Assert
        verify(orderItemRepository, times(1)).findByOrderIdAndProductOptionId(1L, productOptionId);
        assertEquals(OrderStatus.PENDING, orderItem.getStatus());
    }

    @Nested
    @DisplayName("쿠폰 사용")
    class useCoupon {

        @Test
        @DisplayName("쿠폰을 사용하지 않고 주문할 수 있다.")
        void useCoupon_couponIsNull() {

            // Arrange
            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, null, 1000L);
            Order order = new Order(ORDER_ID, USER_ID, null, OrderStatus.CREATED, 1000L, 0L, 1000L);

            when(orderRepository.findById(anyLong())).thenReturn(order);

            // Act
            OrderInfo.Create actualInfo = orderService.applyCoupon(command);

            // Assert
            assertThat(actualInfo.issuedCouponId()).isNull();
        }

        @Test
        @DisplayName("쿠폰 적용 시 주문의 결제 금액이 변경된다.")
        void useCoupon_totalAmountGtDiscountAmount() {

            // Arrange
            Order order = new Order(USER_ID, 10000L);

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 3000L);

            when(orderRepository.findById(anyLong())).thenReturn(order);

            // Act
            OrderInfo.Create actualInfo = orderService.applyCoupon(command);

            // Assert
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(3000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(7000L);
        }

        @Test
        @DisplayName("쿠폰 적용 시 주문 주문금액보다 할인금액이 큰 경우 결제금액은 0으로 변경된다.")
        void useCoupon_totalAmountLtDiscountAmount() {

            // Arrange
            Order order = new Order(USER_ID, 10000L);

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 20000L);

            when(orderRepository.findById(anyLong())).thenReturn(order);

            // Act
            OrderInfo.Create actualInfo = orderService.applyCoupon(command);

            // Assert
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(10000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(0L);
        }

        @Test
        @DisplayName("쿠폰 적용 시 주문 주문금액과 할인금액이 같은 경우 결제금액은 0으로 변경된다.")
        void useCoupon_totalAmountEqDiscountAmount() {

            // Arrange
            Order order = new Order(USER_ID, 10000L);

            OrderCommand.UseCoupon command = new OrderCommand.UseCoupon(ORDER_ID, COUPON_ID, 10000L);

            when(orderRepository.findById(anyLong())).thenReturn(order);

            // Act
            OrderInfo.Create actualInfo = orderService.applyCoupon(command);

            // Assert
            assertThat(actualInfo.issuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actualInfo.totalAmount()).isEqualTo(10000L);
            assertThat(actualInfo.discountAmount()).isEqualTo(10000L);
            assertThat(actualInfo.paymentAmount()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class FindById {

        @Test
        @DisplayName("주문ID로 생성된 주문을 조회한다.")
        void findById_ok() {
            // Arrange
            Order order = new Order(USER_ID, COUPON_ID, 10000L);

            order.pay();

            // Act
            when(orderRepository.findById(anyLong())).thenReturn(order);

            Order actual = orderService.findById(new OrderCommand.Find(ORDER_ID));

            // Assert
            verify(orderRepository,times(1)).findById(ORDER_ID);
            assertThat(actual).isNotNull();
            assertThat(actual.getUserId()).isEqualTo(USER_ID);
            assertThat(actual.getIssuedCouponId()).isEqualTo(COUPON_ID);
            assertThat(actual.getTotalAmount()).isEqualTo(10000L);
            assertThat(actual.getStatus()).isEqualTo(OrderStatus.PAYED);
        }

        @Test
        @DisplayName("주문이 생성되지 않아 주문을 찾을 수 없다.")
        void findById_NotFound() {

            // Arrange
            when(orderRepository.findById(ORDER_ID)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

            // Act
            GlobalException exception = assertThrows(GlobalException.class,
                    () -> orderService.findById(new OrderCommand.Find(ORDER_ID)));

            // Assert
            verify(orderRepository).findById(ORDER_ID);
            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        }
    }

    @Test
    @DisplayName("주문ID로 주문을 결제한다.")
    void pay_ok() {

        // Arrange
        Order order = new Order(USER_ID, COUPON_ID, 10000L);
        order.pay();

        Order mockOrder = mock(Order.class);

        when(orderRepository.findById(ORDER_ID)).thenReturn(mockOrder);
        when(mockOrder.pay()).thenReturn(order);

        // Act
        Order actual = orderService.pay(new OrderCommand.Find(ORDER_ID));

        // Assert
        assertThat(actual).isNotNull();
        assertThat(actual.getUserId()).isEqualTo(USER_ID);
        assertThat(actual.getIssuedCouponId()).isEqualTo(COUPON_ID);
        assertThat(actual.getTotalAmount()).isEqualTo(10000L);
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.PAYED);

        verify(orderRepository).findById(ORDER_ID);
        verify(mockOrder).pay();

        verify(eventPublish).publishOrderComplete(any(OrderEvent.OrderComplete.class));
    }

    @Test
    @DisplayName("생성된 주문이 없어 주문 결제할 수 없다.")
    void pay_NotFound() {

        // Arrange
        when(orderRepository.findById(ORDER_ID)).thenThrow(new GlobalException(ErrorCode.NOT_FOUND));

        // Act
        GlobalException exception = assertThrows(GlobalException.class,
                () -> orderService.pay(new OrderCommand.Find(ORDER_ID)));

        // Assert
        verify(orderRepository).findById(ORDER_ID);
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
    }

    @Test
    @DisplayName("3일간 상위 5개의 인기 판매상품 조회한다.")
    void findBestSelling_ok() {

        // Arrange
        when(orderItemRepository.findBestSelling(3,5))
                .thenReturn(List.of(
                        new OrderInfo.Best(101L, 150L),
                        new OrderInfo.Best(102L, 120L),
                        new OrderInfo.Best(103L, 100L),
                        new OrderInfo.Best(104L, 80L),
                        new OrderInfo.Best(105L, 70L)
                ));

        // Act
        List<OrderInfo.Best> result = orderService.findBestSelling(new OrderCommand.FindBest(3, 5));

        // Assert
        verify(orderItemRepository).findBestSelling(3, 5);

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(5);
    }
}