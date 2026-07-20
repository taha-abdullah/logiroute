package logiroute.logiroute_order.dto;

import logiroute.logiroute_order.domain.enums.OrderStatus;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private String customerId;
    private UUID restaurantId;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
