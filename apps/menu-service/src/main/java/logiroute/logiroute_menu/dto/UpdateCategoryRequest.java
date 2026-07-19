package logiroute.logiroute_menu.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryRequest(
        @NotBlank(message = "Category name is required")
        String name,
        
        Integer sortOrder
) {}
