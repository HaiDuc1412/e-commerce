package fpt.haidd69.ecommerce.services.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fpt.haidd69.ecommerce.constants.AppConstants;
import fpt.haidd69.ecommerce.dto.cart.CartResponse;
import fpt.haidd69.ecommerce.entities.Cart;
import fpt.haidd69.ecommerce.entities.CartItem;
import fpt.haidd69.ecommerce.entities.ProductVariant;
import fpt.haidd69.ecommerce.entities.User;
import fpt.haidd69.ecommerce.exceptions.InsufficientStockException;
import fpt.haidd69.ecommerce.exceptions.ResourceNotFoundException;
import fpt.haidd69.ecommerce.mappers.CartMapper;
import fpt.haidd69.ecommerce.repositories.CartItemRepository;
import fpt.haidd69.ecommerce.repositories.CartRepository;
import fpt.haidd69.ecommerce.repositories.ProductVariantRepository;
import fpt.haidd69.ecommerce.repositories.UserRepository;
import fpt.haidd69.ecommerce.services.CartService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public CartServiceImpl(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductVariantRepository productVariantRepository,
            UserRepository userRepository,
            CartMapper cartMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    /**
     * Get or create cart based on authentication status - If user is logged in:
     * use user-based cart - If user is guest: use session-based cart
     */
    private Cart getOrCreateCart(String sessionId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated (not anonymous or null)
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            String userEmail = auth.getName();
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            // Find or create user-based cart
            return cartRepository.findByUser(user)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        return cartRepository.save(newCart);
                    });
        } else {
            // Guest user: use session-based cart
            // Generate new sessionId if not provided
            String effectiveSessionId = (sessionId != null && !sessionId.trim().isEmpty())
                    ? sessionId
                    : UUID.randomUUID().toString();

            return cartRepository.findBySessionId(effectiveSessionId)
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setSessionId(effectiveSessionId);
                        return cartRepository.save(newCart);
                    });
        }
    }

    @Override
    public CartResponse getCart(String sessionId) {
        Cart cart = getOrCreateCart(sessionId);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(String sessionId, UUID variantId, Integer quantity) {
        Cart cart = getOrCreateCart(sessionId);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Product variant not found"));

        // Check stock availability
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductVariantId(cart.getId(), variantId);

        int totalQuantity = quantity;
        if (existingItem.isPresent()) {
            totalQuantity += existingItem.get().getQuantity();
        }

        if (variant.getAvailableQuantity() < totalQuantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d", variant.getAvailableQuantity()));
        }

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(totalQuantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProductVariant(variant);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        // Flush changes to database and clear persistence context
        entityManager.flush();
        entityManager.clear();

        // Reload cart with all items using JOIN FETCH to ensure items are loaded from database
        Cart updatedCart = cartRepository.findByIdWithItems(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND));
        return cartMapper.toCartResponse(updatedCart);
    }

    @Override
    public CartResponse updateCartItem(String sessionId, UUID itemId, Integer quantity) {
        Cart cart = getOrCreateCart(sessionId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to this cart");
        }

        ProductVariant variant = item.getProductVariant();
        if (variant.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d", variant.getAvailableQuantity()));
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse removeFromCart(String sessionId, UUID itemId) {
        Cart cart = getOrCreateCart(sessionId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to this cart");
        }

        cartItemRepository.delete(item);

        return cartMapper.toCartResponse(cartRepository.findById(cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException(AppConstants.CART_NOT_FOUND)));
    }
}
