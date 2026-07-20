package logiroute.logiroute_order.mapper;

import logiroute.logiroute_order.domain.entity.Order;
import logiroute.logiroute_order.domain.entity.OrderItem;
import logiroute.logiroute_order.dto.OrderCreateRequest;
import logiroute.logiroute_order.dto.OrderItemRequest;
import logiroute.logiroute_order.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemRequest request);

    OrderResponse toResponse(Order order);
}
