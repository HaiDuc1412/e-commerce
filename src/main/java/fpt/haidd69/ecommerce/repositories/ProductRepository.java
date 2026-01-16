package fpt.haidd69.ecommerce.repositories;

import fpt.haidd69.ecommerce.entities.Product;
import fpt.haidd69.ecommerce.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND (:category IS NULL OR p.category = :category) "
            + "AND (:minPrice IS NULL OR p.basePrice >= :minPrice) "
            + "AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice)")
    Page<Product> findByFilters(@Param("category") ProductCategory category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}
