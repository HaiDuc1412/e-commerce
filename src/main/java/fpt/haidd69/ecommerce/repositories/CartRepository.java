package fpt.haidd69.ecommerce.repositories;

import fpt.haidd69.ecommerce.entities.Cart;
import fpt.haidd69.ecommerce.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    Optional<Cart> findBySessionId(String sessionId);

    Optional<Cart> findByUser(User user);
}
