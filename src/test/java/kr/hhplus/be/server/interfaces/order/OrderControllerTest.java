package kr.hhplus.be.server.interfaces.order;

import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.interfaces.order.controller.OrderController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@DisplayName("[단위테스트] OrderController")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;


    @Test
    @DisplayName("상품의 옵션ID와 수량을 입력 받아 주문을 생성한다.")
    void order() throws Exception {

        // Arrange
        String requestBody = """
        {
            "userId": 1,
            "productId": 1,
            "items": [
                {
                    "optionId": 101,
                    "quantity": 10,
                    "unitPrice": 2000
                }
            ],
            "couponId": null
        }
        """;

        String responseBody = """
        {
            "orderId": 10,
            "userId": 1,
            "status": "CREATED"
        }
        """;


        when(orderService.createOrder(any(OrderCommand.Create.class)))
                .thenReturn(new OrderInfo.Create(10L, 1L, null, OrderStatus.CREATED, 20000L, 0L, 20000L));

        // Act & Assert
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }
}