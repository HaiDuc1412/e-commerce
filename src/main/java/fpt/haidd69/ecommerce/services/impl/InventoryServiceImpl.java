package fpt.haidd69.ecommerce.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fpt.haidd69.ecommerce.constants.AppConstants;
import fpt.haidd69.ecommerce.entities.InventoryReservation;
import fpt.haidd69.ecommerce.entities.ProductVariant;
import fpt.haidd69.ecommerce.exceptions.InsufficientStockException;
import fpt.haidd69.ecommerce.exceptions.ResourceNotFoundException;
import fpt.haidd69.ecommerce.repositories.InventoryReservationRepository;
import fpt.haidd69.ecommerce.repositories.ProductVariantRepository;
import fpt.haidd69.ecommerce.services.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final ProductVariantRepository productVariantRepository;
    private final InventoryReservationRepository reservationRepository;

    public InventoryServiceImpl(ProductVariantRepository productVariantRepository,
            InventoryReservationRepository reservationRepository) {
        this.productVariantRepository = productVariantRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    @Override
    public void reserveInventory(String sessionId, UUID variantId, Integer quantity) {
        ProductVariant variant = productVariantRepository.findByIdWithLock(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        if (variant.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d, Requested: %d",
                            variant.getAvailableQuantity(), quantity));
        }

        // Create reservation
        InventoryReservation reservation = new InventoryReservation();
        reservation.setProductVariant(variant);
        reservation.setSessionId(sessionId);
        reservation.setQuantity(quantity);
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(AppConstants.INVENTORY_RESERVATION_MINUTES));
        reservationRepository.save(reservation);

        // Update reserved quantity
        variant.setReservedQuantity(variant.getReservedQuantity() + quantity);
        productVariantRepository.save(variant);
    }

    @Transactional
    @Override
    public void releaseReservation(String sessionId) {
        List<InventoryReservation> reservations = reservationRepository.findBySessionId(sessionId);

        for (InventoryReservation reservation : reservations) {
            ProductVariant variant = reservation.getProductVariant();
            variant.setReservedQuantity(variant.getReservedQuantity() - reservation.getQuantity());
            productVariantRepository.save(variant);
        }

        reservationRepository.deleteBySessionId(sessionId);
    }

    @Transactional
    @Override
    public void confirmReservation(String sessionId) {
        List<InventoryReservation> reservations = reservationRepository.findBySessionId(sessionId);

        for (InventoryReservation reservation : reservations) {
            ProductVariant variant = reservation.getProductVariant();
            // Reduce both stock and reserved quantities
            variant.setStockQuantity(variant.getStockQuantity() - reservation.getQuantity());
            variant.setReservedQuantity(variant.getReservedQuantity() - reservation.getQuantity());
            productVariantRepository.save(variant);
        }

        reservationRepository.deleteBySessionId(sessionId);
    }

    // Scheduled job to clean up expired reservations every 5 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes
    @Transactional
    @Override
    public void releaseExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<InventoryReservation> expiredReservations = reservationRepository.findExpiredReservations(now);

        for (InventoryReservation reservation : expiredReservations) {
            ProductVariant variant = reservation.getProductVariant();
            variant.setReservedQuantity(variant.getReservedQuantity() - reservation.getQuantity());
            productVariantRepository.save(variant);
        }

        reservationRepository.deleteExpiredReservations(now);
    }
}
