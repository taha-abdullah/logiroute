package logiroute.logiroute_menu.dto;

import java.time.Instant;
import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        UUID categoryId,
        String name,
        String description,
        Integer basePriceCents,
        Boolean isVegan,
        Boolean isGlutenFree,
        Boolean isAvailable,
        Instant createdAt,
        Instant updatedAt
) {}
