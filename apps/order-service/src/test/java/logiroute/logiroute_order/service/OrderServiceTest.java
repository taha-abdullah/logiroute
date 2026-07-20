package logiroute.logiroute_order.service;

import jakarta.persistence.EntityNotFoundException;
import logiroute.logiroute_order.domain.entity.Order;
import logiroute.logiroute_order.domain.enums.OrderStatus;
import logiroute.logiroute_order.exception.InvalidOrderStateException;
import logiroute.logiroute_order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrderSetsPendingStatus() {
        Order order = new Order();
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(order);

        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testGetOrderThrowsEntityNotFoundWhenMissing() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrder(orderId));
    }

    @Test
    void testUpdateOrderStatusSuccess() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateOrderStatus(orderId, OrderStatus.ACCEPTED);

        assertEquals(OrderStatus.ACCEPTED, result.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void testUpdateOrderStatusThrowsWhenDelivered() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setStatus(OrderStatus.DELIVERED);
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class, () -> 
            orderService.updateOrderStatus(orderId, OrderStatus.ACCEPTED)
        );
        
        verify(orderRepository, never()).save(any());
    }
}
