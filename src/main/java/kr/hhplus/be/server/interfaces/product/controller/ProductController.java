package kr.hhplus.be.server.interfaces.product.controller;

import kr.hhplus.be.server.application.product.ProductFacade;
import kr.hhplus.be.server.interfaces.product.dto.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kr.hhplus.be.server.interfaces.product.dto.ProductResponse.ProductAggregate;
import static kr.hhplus.be.server.interfaces.product.dto.ProductResponse.ProductList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController implements IProductController {

    private final ProductFacade productFacade;

    @GetMapping
    public ResponseEntity<ProductList> findAll() {
        return ResponseEntity.ok().body(ProductList.from(productFacade.findAll()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductAggregate> findProduct(@PathVariable Long productId) {
        return ResponseEntity.ok().body(ProductAggregate.from(productFacade.findProduct(ProductRequest.Find.toCriteria(productId))));
    }

}
