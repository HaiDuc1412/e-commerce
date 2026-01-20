package fpt.haidd69.ecommerce.entities;

import java.math.BigDecimal;

import fpt.haidd69.ecommerce.enums.Color;
import fpt.haidd69.ecommerce.enums.Size;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_variants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "size", "color"}))
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(unique = true, nullable = false)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Size size;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Color color;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    public Integer getAvailableQuantity() {
        return stockQuantity - reservedQuantity;
    }
}
