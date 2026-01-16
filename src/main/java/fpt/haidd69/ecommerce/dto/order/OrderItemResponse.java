package fpt.haidd69.ecommerce.dto.order;

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
public class OrderItemResponse {

    private UUID id;
    private String productName;
    private String sku;
    private String size;
    private String color;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
}
