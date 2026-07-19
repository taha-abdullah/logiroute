package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ItemOptionRepository extends JpaRepository<ItemOption, UUID> {
    
    // Fetches all options for a given option group
    List<ItemOption> findByOptionGroupId(UUID optionGroupId);
}
