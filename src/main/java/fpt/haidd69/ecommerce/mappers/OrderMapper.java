package fpt.haidd69.ecommerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import fpt.haidd69.ecommerce.dto.order.OrderItemResponse;
import fpt.haidd69.ecommerce.dto.order.OrderResponse;
import fpt.haidd69.ecommerce.entities.Order;
import fpt.haidd69.ecommerce.entities.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "productName", source = "productVariant.product.name")
    @Mapping(target = "sku", source = "productVariant.sku")
    @Mapping(target = "size", source = "productVariant.size", qualifiedByName = "sizeToString")
    @Mapping(target = "color", source = "productVariant.color", qualifiedByName = "colorToString")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);

    List<OrderItemResponse> toOrderItemResponseList(List<OrderItem> items);

    @Named("paymentMethodToString")
    default String paymentMethodToString(fpt.haidd69.ecommerce.enums.PaymentMethod paymentMethod) {
        return paymentMethod != null ? paymentMethod.name() : null;
    }

    @Named("statusToString")
    default String statusToString(fpt.haidd69.ecommerce.enums.OrderStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("sizeToString")
    default String sizeToString(fpt.haidd69.ecommerce.enums.Size size) {
        return size != null ? size.name() : null;
    }

    @Named("colorToString")
    default String colorToString(fpt.haidd69.ecommerce.enums.Color color) {
        return color != null ? color.name() : null;
    }
}
