package logiroute.logiroute_menu.repository;

import logiroute.logiroute_menu.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
}
