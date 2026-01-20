package fpt.haidd69.ecommerce.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fpt.haidd69.ecommerce.dto.cart.AddToCartRequest;
import fpt.haidd69.ecommerce.dto.cart.CartResponse;
import fpt.haidd69.ecommerce.dto.common.ApiResponse;
import fpt.haidd69.ecommerce.services.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@Tag(name = "Cart Management", description = "Shopping cart operations and item management APIs")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Get cart", description = "Retrieve current shopping cart contents by session ID")
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @RequestHeader(value = "Session-Id", required = false) String sessionId) {

        CartResponse cart = cartService.getCart(sessionId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Add to cart", description = "Add a product variant to the shopping cart with specified quantity")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @RequestHeader(value = "Session-Id", required = false) String sessionId,
            @Valid @RequestBody AddToCartRequest request) {

        CartResponse cart = cartService.addToCart(sessionId,
                request.getProductVariantId(),
                request.getQuantity());

        return ResponseEntity.ok(ApiResponse.success(cart, "Item added to cart"));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Update cart item", description = "Update the quantity of an item in the cart")
    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @RequestHeader(value = "Session-Id", required = false) String sessionId,
            @PathVariable UUID itemId,
            @RequestParam Integer quantity) {

        CartResponse cart = cartService.updateCartItem(sessionId, itemId, quantity);
        return ResponseEntity.ok(ApiResponse.success(cart, "Cart updated"));
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Remove from cart", description = "Remove a specific item from the shopping cart")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @RequestHeader(value = "Session-Id", required = false) String sessionId,
            @PathVariable UUID itemId) {

        CartResponse cart = cartService.removeFromCart(sessionId, itemId);
        return ResponseEntity.ok(ApiResponse.success(cart, "Item removed from cart"));
    }
}
