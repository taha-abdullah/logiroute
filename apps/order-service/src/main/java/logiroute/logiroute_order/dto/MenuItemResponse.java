package logiroute.logiroute_order.dto;

import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        UUID categoryId,
        String name,
        Integer basePriceCents,
        Boolean isAvailable
) {}
