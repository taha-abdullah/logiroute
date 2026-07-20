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
        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }
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
        
        validateTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", id, newStatus);
        
        // TODO: Publish RabbitMQ event here for Delivery Service if status == READY_FOR_PICKUP
        
        return updatedOrder;
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.ACCEPTED || next == OrderStatus.CANCELLED;
            case ACCEPTED -> next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;
            case PREPARING -> next == OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP -> next == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };

        if (!valid) {
            throw new InvalidOrderStateException("Invalid transition from " + current + " to " + next);
        }
    }
}
