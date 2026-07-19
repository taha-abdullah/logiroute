package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.OptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OptionGroupRepository extends JpaRepository<OptionGroup, UUID> {
    
    // Fetches all option groups for a given menu item
    List<OptionGroup> findByMenuItemId(UUID menuItemId);
}
