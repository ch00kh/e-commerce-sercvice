package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.domain.order.dto.OrderCommand;
import kr.hhplus.be.server.domain.product.dto.ProductCommand;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.entity.ProductOption;
import kr.hhplus.be.server.domain.product.event.ProductEvent;
import kr.hhplus.be.server.domain.product.event.ProductEventPublisher;
import kr.hhplus.be.server.domain.product.repository.ProductOptionRepository;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final ProductEventPublisher eventPublisher;

    /**
     * 전체 상품 조회
     */
    @Transactional(readOnly = true)
    public ProductInfo.ProductList findAll() {

        List<Product> products = productRepository.findAll();

        List<ProductInfo.ProductAggregate> productAggregates = products.stream().map(product -> {
            List<ProductOption> productOptions = productOptionRepository.findByProductId(product.getId());
            return ProductInfo.ProductAggregate.from(product, productOptions);
        }).toList();

        return ProductInfo.ProductList.of(productAggregates);
    }

    /**
     * 상품 정보 조회
     */
    @Transactional(readOnly = true)
    public ProductInfo.ProductAggregate findProduct(ProductCommand.Find command) {

        Product product = productRepository.findById(command.productId());

        List<ProductOption> productOptions = productOptionRepository.findByProductId(command.productId());

        return ProductInfo.ProductAggregate.from(product, productOptions);
    }

    /**
     * 재고 차감
     */
    @Transactional
    public ProductInfo.Order reduceStock(OrderCommand.Reduce command) {

        ProductInfo.Order productInfo = new ProductInfo.Order(command.orderItems().stream().map(i -> {
            ProductOption productOption = productOptionRepository.findByIdWithPessimisticLock(i.productOptionId());

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

        eventPublisher.publishReduceProductEvent(
                new ProductEvent.ReduceStock(
                        command.orderId(),
                        productInfo.optionDetails()
                )
        );
        return productInfo;
    }

    /**
     * 상품 정보 조회
     */
    @Transactional(readOnly = true)
    public ProductInfo.ProductAggregate findProductByOptionId(ProductCommand.FindByProductOptionId command) {

        ProductOption productOption = productOptionRepository.findById(command.productOptionId());

        Product product = productRepository.findById(productOption.getProductId());

        return ProductInfo.ProductAggregate.from(product, productOption);
    }
}
