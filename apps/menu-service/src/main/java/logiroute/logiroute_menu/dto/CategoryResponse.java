package logiroute.logiroute_menu.dto;

import java.time.Instant;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        UUID restaurantId,
        String name,
        Integer sortOrder,
        Instant createdAt,
        Instant updatedAt
) {}
