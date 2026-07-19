package logiroute.logiroute_menu.dto;

import java.time.Instant;
import java.util.UUID;

public record ItemOptionResponse(
        UUID id,
        UUID optionGroupId,
        String name,
        Integer priceCents,
        Boolean isAvailable,
        Instant createdAt,
        Instant updatedAt
) {}
