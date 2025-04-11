package kr.hhplus.be.server.interfaces.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

//    @Test
//    @DisplayName("[성공] 주문 생성")
//    void order() throws Exception {
//
//        OrderRequest mockRequest = OrderRequest.builder()
//                .userId(1001L)
//                .productId(10001L)
//                .items(List.of(OrderRequest.Item.builder()
//                        .productOptionId(101L)
//                        .quantity(2)
//                        .build()))
//                .couponId(1001L)
//                .build();
//
//        String requestBody = objectMapper.writeValueAsString(mockRequest);
//
//        // When & Then
//        mockMvc.perform(post("/api/order")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.orderId").value(10001))
//                .andExpect(jsonPath("$.userId").value(1001))
//                .andExpect(jsonPath("$.productId").value(10001))
//                .andExpect(jsonPath("$.status").value("PENDING"))
//                .andExpect(jsonPath("$.totalAmount").value("11000"))
//                .andExpect(jsonPath("$.discountAmount").value("1000"))
//                .andExpect(jsonPath("$.paymentAmount").value("10000"));
//    }
}