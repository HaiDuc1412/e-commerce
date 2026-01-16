package fpt.haidd69.ecommerce.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import fpt.haidd69.ecommerce.dto.cart.CartItemResponse;
import fpt.haidd69.ecommerce.dto.cart.CartResponse;
import fpt.haidd69.ecommerce.entities.Cart;
import fpt.haidd69.ecommerce.entities.CartItem;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "totalAmount", source = "items", qualifiedByName = "calculateTotalAmount")
    @Mapping(target = "totalItems", source = "items", qualifiedByName = "calculateTotalItems")
    CartResponse toCartResponse(Cart cart);

    @Mapping(target = "productVariantId", source = "productVariant.id")
    @Mapping(target = "productName", source = "productVariant.product.name")
    @Mapping(target = "sku", source = "productVariant.sku")
    @Mapping(target = "size", source = "productVariant.size", qualifiedByName = "sizeToString")
    @Mapping(target = "color", source = "productVariant.color", qualifiedByName = "colorToString")
    @Mapping(target = "price", source = "productVariant.price")
    @Mapping(target = "subtotal", expression = "java(cartItem.getProductVariant().getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())))")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    List<CartItemResponse> toCartItemResponseList(List<CartItem> items);

    @Named("sizeToString")
    default String sizeToString(fpt.haidd69.ecommerce.enums.Size size) {
        return size != null ? size.name() : null;
    }

    @Named("colorToString")
    default String colorToString(fpt.haidd69.ecommerce.enums.Color color) {
        return color != null ? color.name() : null;
    }

    @Named("calculateTotalAmount")
    default BigDecimal calculateTotalAmount(List<CartItem> items) {
        return items.stream()
                .map(item -> item.getProductVariant().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Named("calculateTotalItems")
    default Integer calculateTotalItems(List<CartItem> items) {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
