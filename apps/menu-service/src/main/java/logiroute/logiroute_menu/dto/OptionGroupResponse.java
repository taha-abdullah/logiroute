package logiroute.logiroute_menu.dto;

import java.time.Instant;
import java.util.UUID;

public record OptionGroupResponse(
        UUID id,
        UUID menuItemId,
        String name,
        Integer minSelectable,
        Integer maxSelectable,
        Instant createdAt,
        Instant updatedAt
) {}
