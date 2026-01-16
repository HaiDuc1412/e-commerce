package fpt.haidd69.ecommerce.services;

import java.util.UUID;

import org.springframework.data.domain.Page;

import fpt.haidd69.ecommerce.dto.order.CheckoutRequest;
import fpt.haidd69.ecommerce.dto.order.OrderResponse;
import fpt.haidd69.ecommerce.enums.OrderStatus;

public interface OrderService {

    OrderResponse createOrder(String sessionId, CheckoutRequest request);

    OrderResponse getOrderByTrackingCode(String trackingCode);

    Page<OrderResponse> getAllOrders(int page, int size);

    Page<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size);

    OrderResponse updateOrderStatus(UUID orderId, String newStatus);

    OrderResponse confirmPayment(UUID orderId, String sessionId);

    void cancelExpiredPendingOrders();
}
