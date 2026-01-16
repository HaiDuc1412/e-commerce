package fpt.haidd69.ecommerce.dto.product;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantResponse {

    private UUID id;
    private String sku;
    private String size;
    private String color;
    private BigDecimal price;
    private Integer availableQuantity;
}
