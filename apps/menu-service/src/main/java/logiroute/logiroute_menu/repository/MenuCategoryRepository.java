package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.MenuCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuCategoryRepository extends JpaRepository<MenuCategory, UUID> {
    
    // Fetches all categories for a given restaurant
    List<MenuCategory> findByRestaurantIdOrderBySortOrderAsc(UUID restaurantId);
}
