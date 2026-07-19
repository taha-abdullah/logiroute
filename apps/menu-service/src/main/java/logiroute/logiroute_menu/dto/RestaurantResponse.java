package logiroute.logiroute_menu.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RestaurantResponse(
        UUID id,
        String name,
        String description,
        List<String> cuisineTags,
        Boolean isOpen,
        BigDecimal latitude,
        BigDecimal longitude,
        Instant createdAt,
        Instant updatedAt
) {}
