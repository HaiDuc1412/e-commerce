package fpt.haidd69.ecommerce.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fpt.haidd69.ecommerce.dto.product.ProductResponse;
import fpt.haidd69.ecommerce.dto.product.ProductVariantResponse;
import fpt.haidd69.ecommerce.entities.Product;
import fpt.haidd69.ecommerce.entities.ProductVariant;
import fpt.haidd69.ecommerce.exceptions.ResourceNotFoundException;
import fpt.haidd69.ecommerce.mappers.ProductMapper;
import fpt.haidd69.ecommerce.repositories.ProductRepository;
import fpt.haidd69.ecommerce.repositories.ProductVariantRepository;
import fpt.haidd69.ecommerce.services.ProductService;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
            ProductVariantRepository productVariantRepository,
            ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productVariantRepository = productVariantRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Page<ProductResponse> getProducts(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<Product> products = productRepository.findByActiveTrue(pageable);
        return products.map(productMapper::toProductResponse);
    }

    @Override
    public Page<ProductResponse> getProductsByFilters(UUID categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> products = productRepository.findByFilters(categoryId, minPrice, maxPrice, pageable);
        return products.map(productMapper::toProductResponse);
    }

    @Override
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toProductResponse(product);
    }

    @Override
    public List<ProductVariantResponse> getProductVariants(UUID productId) {
        List<ProductVariant> variants = productVariantRepository.findByProductIdAndActiveTrue(productId);
        return productMapper.toProductVariantResponseList(variants);
    }
}
