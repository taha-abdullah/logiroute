package logiroute.logiroute_order.web;

import tools.jackson.databind.ObjectMapper;
import logiroute.logiroute_order.domain.entity.Order;
import logiroute.logiroute_order.domain.enums.OrderStatus;
import logiroute.logiroute_order.dto.OrderCreateRequest;
import logiroute.logiroute_order.dto.OrderItemRequest;
import logiroute.logiroute_order.dto.OrderResponse;
import logiroute.logiroute_order.mapper.OrderMapper;
import logiroute.logiroute_order.service.OrderService;
import logiroute.logiroute_order.dto.OrderStatusUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @Test
    void testCreateOrder() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        UUID menuItemId = UUID.randomUUID();
        
        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setMenuItemId(menuItemId);
        itemReq.setQuantity(2);
        itemReq.setPrice(BigDecimal.valueOf(10.50));
        
        OrderCreateRequest request = new OrderCreateRequest();
        request.setRestaurantId(restaurantId);
        request.setItems(List.of(itemReq));

        Order mockOrder = new Order();
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId(UUID.randomUUID());
        mockResponse.setStatus(OrderStatus.PENDING);

        when(orderMapper.toEntity(any(OrderCreateRequest.class))).thenReturn(mockOrder);
        when(orderService.createOrder(any(Order.class))).thenReturn(mockOrder);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockResponse.getId().toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateOrderValidationFails() throws Exception {
        OrderCreateRequest request = new OrderCreateRequest();
        // Missing required restaurantId and items to trigger @Valid constraints

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order mockOrder = new Order();
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId(orderId);
        mockResponse.setStatus(OrderStatus.PENDING);

        when(orderService.getOrder(orderId)).thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        UUID orderId = UUID.randomUUID();
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest(OrderStatus.ACCEPTED);

        Order mockOrder = new Order();
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId(orderId);
        mockResponse.setStatus(OrderStatus.ACCEPTED);

        when(orderService.updateOrderStatus(eq(orderId), eq(OrderStatus.ACCEPTED))).thenReturn(mockOrder);
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockResponse);

        mockMvc.perform(patch("/api/v1/orders/{id}/status", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }

    @Test
    void testGetCustomerOrders() throws Exception {
        String customerId = "mock-customer";
        Order mockOrder = new Order();
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId(UUID.randomUUID());

        when(orderService.getCustomerOrders(customerId)).thenReturn(List.of(mockOrder));
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/orders/customers/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockResponse.getId().toString()));
    }

    @Test
    void testGetRestaurantOrders() throws Exception {
        UUID restaurantId = UUID.randomUUID();
        Order mockOrder = new Order();
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId(UUID.randomUUID());

        when(orderService.getRestaurantOrders(restaurantId)).thenReturn(List.of(mockOrder));
        when(orderMapper.toResponse(mockOrder)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/orders/restaurants/{restaurantId}", restaurantId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockResponse.getId().toString()));
    }
}
