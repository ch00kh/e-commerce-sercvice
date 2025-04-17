package kr.hhplus.be.server.interfaces.payment.controller;

import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.dto.PaymentCriteria;
import kr.hhplus.be.server.application.payment.dto.PaymentResult;
import kr.hhplus.be.server.domain.order.entity.OrderStatus;
import kr.hhplus.be.server.domain.payment.entity.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentFacade paymentFacade;

    @Test
    @DisplayName("[성공] 결제 처리")
    void processPaymentSuccess() throws Exception {

        // Arrange
        String requestBody = """
        {
            "orderId": 1,
            "amount": 1000
        }
        """;

        String responseBody = """
        {
            "orderId": 1,
            "orderStatus": "PAYED",
            "paymentId": 10,
            "paymentStatus": "PAYED"
        }
        """;

        when(paymentFacade.pay(new PaymentCriteria.Pay(1L, 1000L)))
                .thenReturn(new PaymentResult.Pay(10L, 1L, 1000L, OrderStatus.PAYED, PaymentStatus.PAYED, 1000L, LocalDateTime.now()));

        // Act & Assert
        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json(responseBody));
    }
}