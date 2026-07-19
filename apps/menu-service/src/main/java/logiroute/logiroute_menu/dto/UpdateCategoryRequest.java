package logiroute.logiroute_menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCategoryRequest(
        @NotBlank(message = "Category name is required")
        String name,
        
        @NotNull(message = "Sort order is required")
        @PositiveOrZero(message = "Sort order must be positive or zero")
        Integer sortOrder
) {}
