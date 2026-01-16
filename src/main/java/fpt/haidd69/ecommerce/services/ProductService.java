package fpt.haidd69.ecommerce.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;

import fpt.haidd69.ecommerce.dto.product.ProductResponse;
import fpt.haidd69.ecommerce.dto.product.ProductVariantResponse;
import fpt.haidd69.ecommerce.enums.ProductCategory;

public interface ProductService {

    Page<ProductResponse> getProducts(int page, int size, String sortBy);

    Page<ProductResponse> getProductsByFilters(ProductCategory category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size);

    ProductResponse getProductById(UUID id);

    List<ProductVariantResponse> getProductVariants(UUID productId);
}
