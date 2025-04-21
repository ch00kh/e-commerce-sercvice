package kr.hhplus.be.server.domain.product.repository;

import kr.hhplus.be.server.domain.product.entity.Product;

import java.util.List;

public interface ProductRepository {

    Product findById(Long productId);

    List<Product> findAll();

    Product save(Product product);

    void deleteAll();

}
