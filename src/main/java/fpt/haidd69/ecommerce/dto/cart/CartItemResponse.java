package fpt.haidd69.ecommerce.dto.cart;

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
public class CartItemResponse {

    private UUID id;
    private UUID productVariantId;
    private String productName;
    private String sku;
    private String size;
    private String color;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
