package fpt.haidd69.ecommerce.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fpt.haidd69.ecommerce.entities.Order;
import fpt.haidd69.ecommerce.entities.User;
import fpt.haidd69.ecommerce.enums.OrderStatus;

/**
 * Repository for Order entity operations. Includes optimized queries with
 * proper fetching strategies.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Optional<Order> findByTrackingCode(String trackingCode);

    /**
     * Find orders by status with pagination. Uses JOIN FETCH to avoid N+1 query
     * problem.
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.status = :status")
    Page<Order> findByStatusWithItems(@Param("status") OrderStatus status, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Find orders by user with pagination.
     */
    Page<Order> findByUser(User user, Pageable pageable);

    /**
     * Find order with items eagerly loaded to avoid N+1 queries.
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);

    /**
     * Find order with items eagerly loaded by tracking code.
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.trackingCode = :trackingCode")
    Optional<Order> findByTrackingCodeWithItems(@Param("trackingCode") String trackingCode);

    /**
     * Find expired pending payment orders. Orders in PENDING_PAYMENT status
     * that were created before the given time.
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING_PAYMENT' AND o.createdAt < :expiryTime")
    List<Order> findExpiredPendingOrders(@Param("expiryTime") LocalDateTime expiryTime);
}
