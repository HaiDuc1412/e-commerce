package fpt.haidd69.ecommerce.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fpt.haidd69.ecommerce.dto.common.ApiResponse;
import fpt.haidd69.ecommerce.dto.order.OrderResponse;
import fpt.haidd69.ecommerce.enums.OrderStatus;
import fpt.haidd69.ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;

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

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam String status) {

        OrderResponse order = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.success(order, "Order status updated"));
    }
}
