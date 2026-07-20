package logiroute.logiroute_order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OrderItemResponse {
    private UUID id;
    private UUID menuItemId;
    private Integer quantity;
    private BigDecimal price;
    private String specialInstructions;
}
