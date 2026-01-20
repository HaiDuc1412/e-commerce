package fpt.haidd69.ecommerce.dto.cart;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {

    @NotNull(message = "Product variant ID is required")
    private UUID productVariantId;

    @NotNull(message = "Quantity is required")
    private Integer quantity;
}
