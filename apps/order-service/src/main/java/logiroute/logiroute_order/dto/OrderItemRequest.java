package logiroute.logiroute_order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemRequest {
    @NotNull
    private UUID menuItemId;
    
    @NotNull
    @Min(1)
    private Integer quantity;
    
    @NotNull
    private BigDecimal price;
    
    private String specialInstructions;
}
