package logiroute.logiroute_order.repository;

import logiroute.logiroute_order.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    
    List<Order> findByCustomerId(String customerId);
    
    List<Order> findByRestaurantId(UUID restaurantId);
    
}
