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

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import logiroute.logiroute_order.event.OrderReadyEvent;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private logiroute.logiroute_order.client.MenuServiceClient menuServiceClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testCreateOrderSetsPendingStatusAndBackReferences() {
        Order order = new Order();
        logiroute.logiroute_order.domain.entity.OrderItem item = new logiroute.logiroute_order.domain.entity.OrderItem();
        item.setMenuItemId(UUID.randomUUID());
        order.getItems().add(item);
        
        logiroute.logiroute_order.dto.MenuItemResponse mockMenuResponse = new logiroute.logiroute_order.dto.MenuItemResponse(
                item.getMenuItemId(), UUID.randomUUID(), "Burger", 1500, true);
        when(menuServiceClient.getMenuItems(anyList())).thenReturn(java.util.List.of(mockMenuResponse));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(order);

        assertEquals(OrderStatus.PENDING, result.getStatus());
        assertEquals(new java.math.BigDecimal("15"), item.getPrice());
        assertEquals(order, item.getOrder(), "Back-reference should be populated");
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
    void testUpdateOrderStatusThrowsWhenInvalidTransition() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING); // Valid next states: ACCEPTED, CANCELLED
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStateException.class, () -> 
            orderService.updateOrderStatus(orderId, OrderStatus.DELIVERED)
        );
        
        verify(orderRepository, never()).save(any());
    }

    @Test
    void testUpdateOrderStatusToReadyPublishesEvent() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setId(orderId);
        order.setRestaurantId(UUID.randomUUID());
        order.setCustomerId("customer-1");
        order.setStatus(OrderStatus.PREPARING); // Valid next state: READY_FOR_PICKUP
        
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.updateOrderStatus(orderId, OrderStatus.READY_FOR_PICKUP);

        verify(rabbitTemplate).convertAndSend(
                eq("order-events-exchange"),
                eq("order.status.ready"),
                any(OrderReadyEvent.class)
        );
    }
}
