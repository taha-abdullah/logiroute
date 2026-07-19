package logiroute.logiroute_menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record CreateRestaurantRequest(
        @NotBlank(message = "Name is required")
        String name,
        
        String description,
        
        List<String> cuisineTags,
        
        @NotNull(message = "isOpen status is required")
        Boolean isOpen,
        
        @NotNull(message = "Latitude is required")
        BigDecimal latitude,
        
        @NotNull(message = "Longitude is required")
        BigDecimal longitude
) {}
