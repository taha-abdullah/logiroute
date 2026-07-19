package logiroute.logiroute_menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record UpdateRestaurantRequest(
        @NotBlank(message = "Name is required")
        String name,
        
        String description,
        
        List<String> cuisineTags,
        
        @NotNull(message = "isOpen status is required")
        Boolean isOpen
) {}
