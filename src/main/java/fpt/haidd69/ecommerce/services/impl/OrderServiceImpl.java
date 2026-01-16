package fpt.haidd69.ecommerce.services.impl;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fpt.haidd69.ecommerce.constants.AppConstants;
import fpt.haidd69.ecommerce.dto.order.CheckoutRequest;
import fpt.haidd69.ecommerce.dto.order.OrderResponse;
import fpt.haidd69.ecommerce.entities.Cart;
import fpt.haidd69.ecommerce.entities.CartItem;
import fpt.haidd69.ecommerce.entities.Order;
import fpt.haidd69.ecommerce.entities.OrderItem;
import fpt.haidd69.ecommerce.entities.User;
import fpt.haidd69.ecommerce.enums.OrderStatus;
import fpt.haidd69.ecommerce.enums.PaymentMethod;
import fpt.haidd69.ecommerce.exceptions.InvalidOrderStatusException;
import fpt.haidd69.ecommerce.exceptions.ResourceNotFoundException;
import fpt.haidd69.ecommerce.mappers.OrderMapper;
import fpt.haidd69.ecommerce.repositories.CartRepository;
import fpt.haidd69.ecommerce.repositories.OrderRepository;
import fpt.haidd69.ecommerce.repositories.UserRepository;
import fpt.haidd69.ecommerce.services.CartService;
import fpt.haidd69.ecommerce.services.EmailService;
import fpt.haidd69.ecommerce.services.InventoryService;
import fpt.haidd69.ecommerce.services.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
            CartRepository cartRepository,
            CartService cartService,
            InventoryService inventoryService,
            EmailService emailService,
            UserRepository userRepository,
            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.inventoryService = inventoryService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    @Override
    public OrderResponse createOrder(String sessionId, CheckoutRequest request) {
        // Get cart - support both authenticated and guest users
        Cart cart;
        User user = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String userEmail = auth.getName();
            user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null) {
                cart = cartRepository.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));
            } else {
                cart = cartRepository.findBySessionId(sessionId)
                        .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));
            }
        } else {
            cart = cartRepository.findBySessionId(sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));
        }

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        // Reserve inventory for checkout
        for (CartItem cartItem : cart.getItems()) {
            inventoryService.reserveInventory(sessionId,
                    cartItem.getProductVariant().getId(),
                    cartItem.getQuantity());
        }

        // Create order
        Order order = new Order();
        order.setSessionId(sessionId);  // Link order with session for inventory reservation
        order.setUser(user);  // Link to user if authenticated, null if guest
        order.setCustomerName(request.getCustomerName());
        order.setCustomerEmail(request.getCustomerEmail());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod()));
        order.setNotes(request.getNotes());

        // Generate order number and tracking code
        order.setOrderNumber(generateOrderNumber());
        order.setTrackingCode(generateTrackingCode());

        // Calculate total
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(cartItem.getProductVariant());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProductVariant().getPrice());
            orderItem.setSubtotal(cartItem.getProductVariant().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);

        // Set initial status based on payment method
        if (order.getPaymentMethod() == PaymentMethod.COD) {
            // COD orders are automatically confirmed
            order.setStatus(OrderStatus.CONFIRMED);
            inventoryService.confirmReservation(sessionId);
        } else {
            // BANK_TRANSFER and SEPAY require payment confirmation
            order.setStatus(OrderStatus.PENDING_PAYMENT);
        }

        Order savedOrder = orderRepository.save(order);

        // Clear cart
        cartService.clearCart(sessionId);

        // Send confirmation email
        emailService.sendOrderConfirmation(savedOrder);

        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderResponse getOrderByTrackingCode(String trackingCode) {
        Order order = orderRepository.findByTrackingCode(trackingCode)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND));
        return orderMapper.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderResponse> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findAll(pageable);
        return orders.map(orderMapper::toOrderResponse);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findByStatus(status, pageable);
        return orders.map(orderMapper::toOrderResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(UUID orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND));

        OrderStatus status = OrderStatus.valueOf(newStatus);

        // Validate status transition
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        // Send status update email
        emailService.sendOrderStatusUpdate(updatedOrder);

        return orderMapper.toOrderResponse(updatedOrder);
    }

    @Override
    public OrderResponse confirmPayment(UUID orderId, String sessionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new InvalidOrderStatusException("Order is not pending payment");
        }

        order.setStatus(OrderStatus.PAID);
        inventoryService.confirmReservation(sessionId);
        Order updatedOrder = orderRepository.save(order);

        emailService.sendPaymentConfirmation(updatedOrder);

        return orderMapper.toOrderResponse(updatedOrder);
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus newStatus) {
        // Define valid transitions
        if (current == OrderStatus.CANCELLED || current == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException(
                    String.format("Cannot change status from %s to %s", current, newStatus));
        }
    }

    /**
     * Generate unique order number with format: ORD-YYYYMMDD-XXXXXX Example:
     * ORD-20260116-123456
     */
    private String generateOrderNumber() {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomSuffix = String.format("%06d", (int) (Math.random() * 1000000));
        return "ORD-" + timestamp + "-" + randomSuffix;
    }

    /**
     * Generate unique tracking code with format: TRK-XXXXXXXXXX Example:
     * TRK-A1B2C3D4E5
     */
    private String generateTrackingCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder("TRK-");
        for (int i = 0; i < 10; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    /**
     * Scheduled job to automatically cancel orders that are pending payment for
     * too long. Runs every 5 minutes to check for expired orders.
     */
    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    @Override
    public void cancelExpiredPendingOrders() {
        java.time.LocalDateTime expiryTime = java.time.LocalDateTime.now()
                .minusMinutes(AppConstants.INVENTORY_RESERVATION_MINUTES);

        java.util.List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(expiryTime);

        for (Order order : expiredOrders) {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);

            // Release inventory reservation if it still exists
            if (order.getSessionId() != null) {
                try {
                    inventoryService.releaseReservation(order.getSessionId());
                } catch (Exception e) {
                    // Reservation might already be released by scheduled job, that's ok
                }
            }
        }

        if (!expiredOrders.isEmpty()) {
            System.out.println("Auto-cancelled " + expiredOrders.size() + " expired pending orders");
        }
    }
}
