package logiroute.logiroute_menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateOptionGroupRequest(
        @NotBlank(message = "Group name is required")
        String name,
        
        @NotNull(message = "Min selectable is required")
        @PositiveOrZero(message = "Min selectable must be positive or zero")
        Integer minSelectable,
        
        Integer maxSelectable
) {}
