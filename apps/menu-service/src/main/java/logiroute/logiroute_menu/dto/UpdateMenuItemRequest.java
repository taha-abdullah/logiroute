package logiroute.logiroute_menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateMenuItemRequest(
        @NotBlank(message = "Item name is required")
        String name,
        
        String description,
        
        @NotNull(message = "Base price is required")
        @PositiveOrZero(message = "Price must be positive or zero")
        Integer basePriceCents,
        
        Boolean isVegan,
        
        Boolean isGlutenFree,
        
        @NotNull(message = "isAvailable status is required")
        Boolean isAvailable
) {
    public UpdateMenuItemRequest {
        if (isVegan == null) isVegan = false;
        if (isGlutenFree == null) isGlutenFree = false;
    }
}
