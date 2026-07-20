package logiroute.logiroute_order.event;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderReadyEvent(
        String eventId,
        OffsetDateTime timestamp,
        UUID orderId,
        UUID restaurantId,
        String customerId,
        Integer totalItems,
        BigDecimal estimatedValue
) {
    public OrderReadyEvent(UUID orderId, UUID restaurantId, String customerId, Integer totalItems, BigDecimal estimatedValue) {
        this(UUID.randomUUID().toString(), OffsetDateTime.now(), orderId, restaurantId, customerId, totalItems, estimatedValue);
    }
}
