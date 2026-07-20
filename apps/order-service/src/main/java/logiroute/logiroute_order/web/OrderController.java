package logiroute.logiroute_order.web;

import jakarta.validation.Valid;
import logiroute.logiroute_order.domain.entity.Order;
import logiroute.logiroute_order.dto.OrderCreateRequest;
import logiroute.logiroute_order.dto.OrderResponse;
import logiroute.logiroute_order.mapper.OrderMapper;
import logiroute.logiroute_order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        Order order = orderMapper.toEntity(request);
        
        // Mocking the authenticated user's ID for now until Spring Security/Keycloak is wired up
        order.setCustomerId("mock-customer-id");
        
        Order savedOrder = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderMapper.toResponse(savedOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return ResponseEntity.ok(orderMapper.toResponse(order));
    }
}
