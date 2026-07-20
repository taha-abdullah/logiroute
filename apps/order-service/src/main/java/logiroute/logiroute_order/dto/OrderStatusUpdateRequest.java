package logiroute.logiroute_order.dto;

import jakarta.validation.constraints.NotNull;
import logiroute.logiroute_order.domain.enums.OrderStatus;

public record OrderStatusUpdateRequest(
        @NotNull(message = "New status is required")
        OrderStatus status
) {}
