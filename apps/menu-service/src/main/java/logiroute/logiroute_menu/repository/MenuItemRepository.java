package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    
    // Fetches all items for a given category
    List<MenuItem> findByCategoryId(UUID categoryId);
}
