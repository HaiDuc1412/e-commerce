package fpt.haidd69.ecommerce.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import fpt.haidd69.ecommerce.entities.Cart;
import fpt.haidd69.ecommerce.entities.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    @Query("SELECT c FROM Cart c "
            + "LEFT JOIN FETCH c.items ci "
            + "LEFT JOIN FETCH ci.productVariant pv "
            + "LEFT JOIN FETCH pv.product p "
            + "LEFT JOIN FETCH p.category "
            + "WHERE c.sessionId = :sessionId")
    Optional<Cart> findBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT c FROM Cart c "
            + "LEFT JOIN FETCH c.items ci "
            + "LEFT JOIN FETCH ci.productVariant pv "
            + "LEFT JOIN FETCH pv.product p "
            + "LEFT JOIN FETCH p.category "
            + "WHERE c.user = :user")
    Optional<Cart> findByUser(@Param("user") User user);

    @Query("SELECT c FROM Cart c "
            + "LEFT JOIN FETCH c.items ci "
            + "LEFT JOIN FETCH ci.productVariant pv "
            + "LEFT JOIN FETCH pv.product p "
            + "LEFT JOIN FETCH p.category "
            + "WHERE c.id = :id")
    Optional<Cart> findByIdWithItems(@Param("id") UUID id);
}
