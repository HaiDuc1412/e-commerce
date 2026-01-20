package fpt.haidd69.ecommerce.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fpt.haidd69.ecommerce.dto.common.ApiResponse;
import fpt.haidd69.ecommerce.dto.order.OrderResponse;
import fpt.haidd69.ecommerce.dto.order.UpdateOrderRequest;
import fpt.haidd69.ecommerce.enums.OrderStatus;
import fpt.haidd69.ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/orders")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Management", description = "Admin order management APIs - Requires ADMIN role")
public class AdminController {

    private final OrderService orderService;

    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(
            summary = "Get all orders",
            description = "Retrieve paginated list of all orders with optional status filter. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {

        Page<OrderResponse> orders;
        if (status != null) {
            orders = orderService.getOrdersByStatus(OrderStatus.valueOf(status), page, size);
        } else {
            orders = orderService.getAllOrders(page, size);
        }

        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @Operation(
            summary = "Get order details",
            description = "Retrieve detailed information of a specific order by ID. Requires ADMIN role.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable UUID orderId) {

        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @Operation(
            summary = "Update order status",
            description = "Confirm payment or cancel order. Requires ADMIN role. Action must be 'CONFIRM' or 'CANCEL'.",
            security = {
                @SecurityRequirement(name = "bearerAuth"),
                @SecurityRequirement(name = "Session-Id")
            }
    )
    @PostMapping("/{orderId}/update-status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestHeader("Session-Id") String sessionId,
            @Valid @RequestBody UpdateOrderRequest request) {

        OrderResponse order;
        String message;

        if ("CONFIRM".equals(request.getAction())) {
            order = orderService.confirmPayment(orderId, sessionId);
            message = "Payment confirmed successfully";
        } else if ("CANCEL".equals(request.getAction())) {
            order = orderService.cancelOrder(orderId, sessionId);
            message = "Order cancelled successfully";
        } else {
            throw new IllegalArgumentException("Invalid action. Must be CONFIRM or CANCEL");
        }

        return ResponseEntity.ok(ApiResponse.success(order, message));
    }
}
