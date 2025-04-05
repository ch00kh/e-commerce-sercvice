package kr.hhplus.be.server.api.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("[성공] 상품 목록 조회")
    void findAll() throws Exception {

        mockMvc.perform(get("/api/product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productId").value(1002))
                .andExpect(jsonPath("$[0].brand").value("총각쓰떡"))
                .andExpect(jsonPath("$[0].name").value("백설기"))

                .andExpect(jsonPath("$[0].options").isArray())
                .andExpect(jsonPath("$[0].options.length()").value(2))
                .andExpect(jsonPath("$[0].options[0].productDetailId").value(101))
                .andExpect(jsonPath("$[0].options[0].optionValue").value("백설기/10개"))
                .andExpect(jsonPath("$[0].options[0].price").value(5500))
                .andExpect(jsonPath("$[0].options[0].stock").value(100))
                .andExpect(jsonPath("$[0].options[1].productDetailId").value(102))
                .andExpect(jsonPath("$[0].options[1].optionValue").value("우유설기/10개"))
                .andExpect(jsonPath("$[0].options[1].price").value(5900))
                .andExpect(jsonPath("$[0].options[1].stock").value(99))

                .andExpect(jsonPath("$[1].productId").value(1002))
                .andExpect(jsonPath("$[1].brand").value("총각쓰떡"))
                .andExpect(jsonPath("$[1].name").value("백일떡"))
                .andExpect(jsonPath("$[1].options").isArray())
                .andExpect(jsonPath("$[1].options.length()").value(1))
                .andExpect(jsonPath("$[1].options[0].productDetailId").value(201))
                .andExpect(jsonPath("$[1].options[0].optionValue").value("백일떡/10개"))
                .andExpect(jsonPath("$[1].options[0].price").value(13700))
                .andExpect(jsonPath("$[1].options[0].stock").value(200));
    }
    
    @Test
    @DisplayName("[성공] 상품 정보 조회")
    void find() throws Exception {

        mockMvc.perform(get("/api/product/{id}", 1002L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1002))
                .andExpect(jsonPath("$.brand").value("총각쓰떡"))
                .andExpect(jsonPath("$.name").value("백일떡"))
                .andExpect(jsonPath("$.options.length()").value(1))
                .andExpect(jsonPath("$.options[0].productDetailId").value(201))
                .andExpect(jsonPath("$.options[0].optionValue").value("백일떡/10개"))
                .andExpect(jsonPath("$.options[0].price").value(13700))
                .andExpect(jsonPath("$.options[0].stock").value(200));
    }

    @Test
    @DisplayName("[성공] 인기 상품 조회")
    void findBestProductsWithDefaultParams() throws Exception {

        mockMvc.perform(get("/api/product/best"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))

                .andExpect(jsonPath("$[0].productId").value(1001))
                .andExpect(jsonPath("$[0].brand").value("총각쓰떡"))
                .andExpect(jsonPath("$[0].name").value("백설기"))
                .andExpect(jsonPath("$[0].totalOrders").value(100))
                .andExpect(jsonPath("$[0].option.detailId").value(101))
                .andExpect(jsonPath("$[0].option.optionValue").value("백설기/10개"))
                .andExpect(jsonPath("$[0].option.price").value(5500))
                .andExpect(jsonPath("$[0].option.stock").value(100))

                .andExpect(jsonPath("$[1].productId").value(1001))
                .andExpect(jsonPath("$[1].brand").value("총각쓰떡"))
                .andExpect(jsonPath("$[1].name").value("백설기"))
                .andExpect(jsonPath("$[1].totalOrders").value(86))
                .andExpect(jsonPath("$[1].option.detailId").value(102))
                .andExpect(jsonPath("$[1].option.optionValue").value("우유설기/10개"))
                .andExpect(jsonPath("$[1].option.price").value(5900))
                .andExpect(jsonPath("$[1].option.stock").value(99))

                .andExpect(jsonPath("$[2].productId").value(1001))
                .andExpect(jsonPath("$[2].brand").value("총각쓰떡"))
                .andExpect(jsonPath("$[2].name").value("백일떡"))
                .andExpect(jsonPath("$[2].totalOrders").value(32))
                .andExpect(jsonPath("$[2].option.detailId").value(201))
                .andExpect(jsonPath("$[2].option.optionValue").value("백일떡/10개"))
                .andExpect(jsonPath("$[2].option.price").value(13700))
                .andExpect(jsonPath("$[2].option.stock").value(92));
    }


}