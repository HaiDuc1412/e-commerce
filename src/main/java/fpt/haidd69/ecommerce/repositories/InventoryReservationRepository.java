package fpt.haidd69.ecommerce.repositories;

import fpt.haidd69.ecommerce.entities.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {

    List<InventoryReservation> findBySessionId(String sessionId);

    @Query("SELECT ir FROM InventoryReservation ir WHERE ir.expiresAt < :now")
    List<InventoryReservation> findExpiredReservations(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM InventoryReservation ir WHERE ir.expiresAt < :now")
    void deleteExpiredReservations(@Param("now") LocalDateTime now);

    @Modifying
    @Query("DELETE FROM InventoryReservation ir WHERE ir.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") String sessionId);
}
