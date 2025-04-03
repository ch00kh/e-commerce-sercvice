package kr.hhplus.be.server.api.product.controller;

import kr.hhplus.be.server.api.product.dto.BestProductResponse;
import kr.hhplus.be.server.api.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController implements ProductSpecification {

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {

        ProductResponse item1 = ProductResponse.builder()
                .productId(1002L)
                .brand("총각쓰떡")
                .name("백설기")
                .options(List.of(
                        ProductResponse.Options.builder()
                                .productDetailId(101L)
                                .optionValue("백설기/10개")
                                .price(5500L)
                                .stock(100L)
                                .build(),
                        ProductResponse.Options.builder()
                                .productDetailId(102L)
                                .optionValue("우유설기/10개")
                                .price(5900L)
                                .stock(99L)
                                .build()
                ))
                .build();

        ProductResponse item2 = ProductResponse.builder()
                .productId(1002L)
                .brand("총각쓰떡")
                .name("백일떡")
                .options(List.of(
                        ProductResponse.Options.builder()
                                .productDetailId(201L)
                                .optionValue("백일떡/10개")
                                .price(13700L)
                                .stock(200L)
                                .build()
                ))
                .build();

        return ResponseEntity.ok().body(List.of(item1, item2));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> find(@PathVariable Long id) {

        ProductResponse mock = ProductResponse.builder()
                .productId(1002L)
                .brand("총각쓰떡")
                .name("백일떡")
                .options(List.of(
                        ProductResponse.Options.builder()
                                .productDetailId(201L)
                                .optionValue("백일떡/10개")
                                .price(13700L)
                                .stock(200L)
                                .build()
                ))
                .build();

        return ResponseEntity.ok().body(mock);
    }

    @GetMapping("/best")
    public ResponseEntity<List<BestProductResponse>> findBest(
            @RequestParam(value = "days", defaultValue = "3") Integer days,
            @RequestParam(value = "limit", defaultValue = "5") Integer limit
    ) {

        BestProductResponse item1 = BestProductResponse.builder()
                .productId(1001L)
                .brand("총각쓰떡")
                .name("백설기")
                .totalOrders(100)
                .option(BestProductResponse.Option.builder()
                        .detailId(101L)
                        .optionValue("백설기/10개")
                        .price(5500L)
                        .stock(100L)
                        .build())
                .build();

        BestProductResponse item2 = BestProductResponse.builder()
                .productId(1001L)
                .brand("총각쓰떡")
                .name("백설기")
                .totalOrders(86)
                .option(BestProductResponse.Option.builder()
                        .detailId(102L)
                        .optionValue("우유설기/10개")
                        .price(5900L)
                        .stock(99L)
                        .build())
                .build();

        BestProductResponse item3 = BestProductResponse.builder()
                .productId(1001L)
                .brand("총각쓰떡")
                .name("백일떡")
                .totalOrders(32)
                .option(BestProductResponse.Option.builder()
                        .detailId(201L)
                        .optionValue("백일떡/10개")
                        .price(13700L)
                        .stock(92L)
                        .build())
                .build();

        List<BestProductResponse> bestProducts = List.of(item1, item2, item3);

        return ResponseEntity.ok(bestProducts);
    }

}
