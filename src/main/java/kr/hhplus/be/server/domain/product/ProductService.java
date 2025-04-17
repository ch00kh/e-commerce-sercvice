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
    public ProductInfo.Order reduceStock(List<OrderCommand.OrderItem> command) {

        return new ProductInfo.Order(command.stream().map(i -> {
            ProductOption productOption = productOptionRepository.findById(i.productOptionId())
                    .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

            if (productOption.canPurchase(i.quantity())) {
                Long remainingStock = productOption.reduceStock(i.quantity());

                return new ProductInfo.OptionDetail(
                        productOption.getId(),
                        true,
                        i.quantity(),
                        remainingStock
                );
            } else {
                return new ProductInfo.OptionDetail(
                        productOption.getId(),
                        false,
                        i.quantity(),
                        productOption.getStock()
                );
            }
        }).toList());
    }

    @Transactional(readOnly = true)
    public ProductInfo.ProductAggregate findProductByOptionId(ProductCommand.FindByProductOptionId command) {

        ProductOption productOption = productOptionRepository.findById(command.productOptionId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        Product product = productRepository.findById(productOption.getProductId())
                .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));

        return ProductInfo.ProductAggregate.from(product, productOption);
    }
}
