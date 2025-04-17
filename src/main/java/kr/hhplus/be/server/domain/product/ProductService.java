package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import kr.hhplus.be.server.global.exception.ErrorCode;
import kr.hhplus.be.server.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    @Transactional(readOnly = true)
    public ProductInfo.ProductList findAll() {

        List<Product> products = productRepository.findAll();

        List<ProductInfo.ProductAggregate> productAggregates = products.stream().map(product -> {
            List<ProductOption> productOptions = productOptionRepository.findByProductId(product.getId());
            return ProductInfo.ProductAggregate.from(product, productOptions);
        }).toList();

        return ProductInfo.ProductList.of(productAggregates);
    }

    @Transactional(readOnly = true)
    public ProductInfo.ProductAggregate findProduct(ProductCommand.Find command) {

        Product product = productRepository.findById(command.productId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        List<ProductOption> productOptions = productOptionRepository.findByProductId(command.productId());

        return ProductInfo.ProductAggregate.from(product, productOptions);
    }

    @Transactional
    public ProductInfo.CheckedProductOrder reduceStock(List<OrderCommand.OrderItem> command) {

        return new ProductInfo.CheckedProductOrder (command.stream().map(i -> {
            ProductOption productOptions = productOptionRepository.findById(i.productOptionId())
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

            Integer remainingStock = productOptions.reduceStock(i.quantity());

            return ProductInfo.CheckedStock.builder()
                    .optionId(productOptions.getId())
                    .isEnough(remainingStock > 0)
                    .requestQuantity(i.quantity())
                    .remainingQuantity(remainingStock)
                    .build();

        }).toList());
    }
}
