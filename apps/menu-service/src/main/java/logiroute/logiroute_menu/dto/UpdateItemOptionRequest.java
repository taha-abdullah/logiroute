package logiroute.logiroute_menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateItemOptionRequest(
        @NotBlank(message = "Option name is required")
        String name,
        
        @NotNull(message = "Price is required")
        @PositiveOrZero(message = "Price must be positive or zero")
        Integer priceCents,
        
        @NotNull(message = "isAvailable status is required")
        Boolean isAvailable
) {}
