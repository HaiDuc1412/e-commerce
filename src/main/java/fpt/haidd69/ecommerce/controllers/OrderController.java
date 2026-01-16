package fpt.haidd69.ecommerce.controllers;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fpt.haidd69.ecommerce.dto.common.ApiResponse;
import fpt.haidd69.ecommerce.dto.order.CheckoutRequest;
import fpt.haidd69.ecommerce.dto.order.OrderResponse;
import fpt.haidd69.ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Order controller for checkout and order tracking. Supports both authenticated
 * and guest orders.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "Order checkout and tracking APIs")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Create order", description = "Checkout and create new order from cart")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @RequestHeader("Session-Id") String sessionId,
            @Valid @RequestBody CheckoutRequest request) {

        OrderResponse order = orderService.createOrder(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success(order, "Order created successfully"));
    }

    @Operation(summary = "Track order", description = "Get order details by tracking code")
    @GetMapping("/track/{trackingCode}")
    public ResponseEntity<ApiResponse<OrderResponse>> trackOrder(
            @PathVariable String trackingCode) {

        OrderResponse order = orderService.getOrderByTrackingCode(trackingCode);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @Operation(summary = "Confirm payment", description = "Confirm payment for orders with BANK_TRANSFER or SEPAY method")
    @PostMapping("/{orderId}/confirm-payment")
    public ResponseEntity<ApiResponse<OrderResponse>> confirmPayment(
            @PathVariable UUID orderId,
            @RequestHeader("Session-Id") String sessionId) {

        OrderResponse order = orderService.confirmPayment(orderId, sessionId);
        return ResponseEntity.ok(ApiResponse.success(order, "Payment confirmed successfully"));
    }
}
