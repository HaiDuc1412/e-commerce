package fpt.haidd69.ecommerce.services;

import java.util.UUID;

public interface InventoryService {

    void reserveInventory(String sessionId, UUID variantId, Integer quantity);

    void releaseReservation(String sessionId);

    void confirmReservation(String sessionId);

    void releaseExpiredReservations();
}
