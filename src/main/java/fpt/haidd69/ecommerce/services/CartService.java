package fpt.haidd69.ecommerce.services;

import java.util.UUID;

import fpt.haidd69.ecommerce.dto.cart.CartResponse;

public interface CartService {

    CartResponse getCart(String sessionId);

    CartResponse addToCart(String sessionId, UUID variantId, Integer quantity);

    CartResponse updateCartItem(String sessionId, UUID itemId, Integer quantity);

    CartResponse removeFromCart(String sessionId, UUID itemId);
}
