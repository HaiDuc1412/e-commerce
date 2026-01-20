package fpt.haidd69.ecommerce.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderRequest {

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "CONFIRM|CANCEL", message = "Action must be either CONFIRM or CANCEL")
    private String action;
}
