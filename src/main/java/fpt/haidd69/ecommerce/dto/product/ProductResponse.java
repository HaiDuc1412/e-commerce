package fpt.haidd69.ecommerce.dto.product;

import java.math.BigDecimal;
import java.util.List;
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
public class ProductResponse {

    private UUID id;
    private String name;
    private String description;
    private String category;
    private BigDecimal basePrice;
    private String imageUrl;
    private List<ProductVariantResponse> variants;
}
