package fpt.haidd69.ecommerce.repositories;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fpt.haidd69.ecommerce.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Page<Product> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND (:categoryId IS NULL OR p.category.id = :categoryId) "
            + "AND (:minPrice IS NULL OR p.basePrice >= :minPrice) "
            + "AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice)")
    Page<Product> findByFilters(@Param("categoryId") UUID categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable);
}
