package logiroute.logiroute_order.service;

import jakarta.persistence.EntityNotFoundException;
import logiroute.logiroute_order.domain.entity.Order;
import logiroute.logiroute_order.domain.enums.OrderStatus;
import logiroute.logiroute_order.repository.OrderRepository;
import logiroute.logiroute_order.exception.InvalidOrderStateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);
        log.info("Created new order with ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + id));
    }

    @Transactional
    public Order updateOrderStatus(UUID id, OrderStatus newStatus) {
        Order order = getOrder(id);
        
        // Simple state machine validation could be added here
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new InvalidOrderStateException("Cannot change status of a " + order.getStatus() + " order");
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", id, newStatus);
        
        // TODO: Publish RabbitMQ event here for Delivery Service if status == READY_FOR_PICKUP
        
        return updatedOrder;
    }
}
