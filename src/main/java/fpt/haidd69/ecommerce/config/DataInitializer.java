package fpt.haidd69.ecommerce.config;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import fpt.haidd69.ecommerce.entities.Category;
import fpt.haidd69.ecommerce.entities.Product;
import fpt.haidd69.ecommerce.entities.ProductVariant;
import fpt.haidd69.ecommerce.entities.User;
import fpt.haidd69.ecommerce.enums.Color;
import fpt.haidd69.ecommerce.enums.Role;
import fpt.haidd69.ecommerce.enums.Size;
import fpt.haidd69.ecommerce.repositories.CategoryRepository;
import fpt.haidd69.ecommerce.repositories.ProductRepository;
import fpt.haidd69.ecommerce.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Database initializer that runs on application startup. Creates default users
 * and products if they don't exist.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.init.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.init.customer.password:Customer@123}")
    private String customerPassword;

    @Bean
    @SuppressWarnings("unused")
    CommandLineRunner initDatabase() {
        return args -> {
            log.info("=== Starting database initialization ===");
            initUsers();
            initProductCatalog();
            log.info("=== Database initialization completed ===");
        };
    }

    /**
     * Initialize default users for testing
     */
    private void initUsers() {
        String adminEmail = "admin@ecommerce.com";
        String customerEmail = "customer@ecommerce.com";

        // Create admin user if not exists
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .fullName("System Administrator")
                    .phone("0901234567")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
            log.info("Created default admin user: {}", adminEmail);
        }

        // Create customer user if not exists
        if (!userRepository.existsByEmail(customerEmail)) {
            User customer = User.builder()
                    .email(customerEmail)
                    .password(passwordEncoder.encode(customerPassword))
                    .fullName("Test Customer")
                    .phone("0987654321")
                    .role(Role.CUSTOMER)
                    .active(true)
                    .build();

            userRepository.save(customer);
            log.info("Created default customer user: {}", customerEmail);
        }
    }

    /**
     * Initialize product catalog with categories and products
     */
    private void initProductCatalog() {
        if (productRepository.count() > 0) {
            log.info("Products already exist, skipping initialization");
            return;
        }

        log.info("Initializing product catalog...");

        // Create and SAVE Categories first
        Category tshirtCategory = saveCategory("Áo Thun", "Áo thun cotton cao cấp, form rộng thoải mái");
        Category hoodieCategory = saveCategory("Hoodie", "Áo hoodie chất liệu nỉ bông ấm áp");
        Category jacketCategory = saveCategory("Áo Khoác", "Áo khoác đa dạng kiểu dáng");

        // Create T-Shirt Products
        createProduct("Basic Tee - Essential",
                "Áo thun basic trơn, chất liệu cotton 100%, form rộng oversized phù hợp mọi dáng người",
                tshirtCategory,
                new BigDecimal("199000"),
                "https://product.hstatic.net/200000033444/product/1_f91fb0c1ad054e1da7d27c5cc5317b27_master.jpg",
                new Color[]{Color.BLACK, Color.WHITE, Color.GRAY},
                new Size[]{Size.M, Size.L, Size.XL, Size.XXL},
                50);

        createProduct("Graphic Tee - Street Culture",
                "Áo thun in hình nghệ thuật đường phố, thiết kế độc đáo, limited edition",
                tshirtCategory,
                new BigDecimal("249000"),
                "https://product.hstatic.net/200000033444/product/2_be7d6fa3e9c74889ace44c46c1b6e7a4_master.jpg",
                new Color[]{Color.BLACK, Color.WHITE},
                new Size[]{Size.M, Size.L, Size.XL},
                30);

        createProduct("Premium Tee - Heavyweight",
                "Áo thun cao cấp vải dày dặn, form boxy, chất liệu cotton compact cao cấp",
                tshirtCategory,
                new BigDecimal("299000"),
                "https://product.hstatic.net/200000033444/product/3_c5d4e8a9b6f74d2eb3f8c7a1d2e9f5b8_master.jpg",
                new Color[]{Color.BLACK, Color.GRAY, Color.BLUE},
                new Size[]{Size.L, Size.XL, Size.XXL},
                40);

        // Create Hoodie Products
        createProduct("Classic Hoodie - Daily Wear",
                "Hoodie nỉ bông basic, mũ to bản, túi kangaroo tiện dụng, giữ ấm tốt",
                hoodieCategory,
                new BigDecimal("399000"),
                "https://product.hstatic.net/200000033444/product/hoodie1_master.jpg",
                new Color[]{Color.BLACK, Color.GRAY, Color.BLUE},
                new Size[]{Size.M, Size.L, Size.XL},
                35);

        createProduct("Premium Hoodie - Embroidered Logo",
                "Hoodie cao cấp thêu logo nổi, chất nỉ bông dày dặn, form rộng thoải mái",
                hoodieCategory,
                new BigDecimal("499000"),
                "https://product.hstatic.net/200000033444/product/hoodie2_master.jpg",
                new Color[]{Color.BLACK, Color.WHITE},
                new Size[]{Size.L, Size.XL, Size.XXL},
                25);

        createProduct("Oversized Hoodie - Unisex",
                "Hoodie oversize phong cách Hàn Quốc, form rộng unisex, chất nỉ cao cấp",
                hoodieCategory,
                new BigDecimal("449000"),
                "https://product.hstatic.net/200000033444/product/hoodie3_master.jpg",
                new Color[]{Color.GRAY, Color.PINK, Color.BLUE},
                new Size[]{Size.L, Size.XL},
                20);

        // Create Jacket Products
        createProduct("Windbreaker Jacket - Lightweight",
                "Áo khoác gió nhẹ, chống nước tốt, phù hợp mùa xuân hè",
                jacketCategory,
                new BigDecimal("549000"),
                "https://product.hstatic.net/200000033444/product/jacket1_master.jpg",
                new Color[]{Color.BLACK, Color.BLUE, Color.GREEN},
                new Size[]{Size.M, Size.L, Size.XL},
                30);

        createProduct("Bomber Jacket - Classic Style",
                "Áo bomber jacket phong cách cổ điển, chất liệu dù cao cấp, lót nỉ ấm",
                jacketCategory,
                new BigDecimal("699000"),
                "https://product.hstatic.net/200000033444/product/jacket2_master.jpg",
                new Color[]{Color.BLACK, Color.GREEN},
                new Size[]{Size.L, Size.XL},
                15);

        createProduct("Denim Jacket - Vintage Wash",
                "Áo khoác jean wash vintage, kiểu dáng oversized, chất jean dày dặn",
                jacketCategory,
                new BigDecimal("599000"),
                "https://product.hstatic.net/200000033444/product/jacket3_master.jpg",
                new Color[]{Color.BLUE, Color.BLACK},
                new Size[]{Size.M, Size.L, Size.XL},
                20);

        createProduct("Coach Jacket - Minimal Design",
                "Áo khoác coach jacket tối giản, nhẹ nhàng, phù hợp layering",
                jacketCategory,
                new BigDecimal("479000"),
                "https://product.hstatic.net/200000033444/product/jacket4_master.jpg",
                new Color[]{Color.BLACK, Color.GRAY, Color.YELLOW},
                new Size[]{Size.M, Size.L, Size.XL, Size.XXL},
                25);

        log.info("Created 10 products with multiple variants");
    }

    private Category saveCategory(String name, String description) {
        // Check if category already exists
        return categoryRepository.findByName(name)
                .orElseGet(() -> {
                    Category category = Category.builder()
                            .name(name)
                            .description(description)
                            .active(true)
                            .build();

                    Category saved = categoryRepository.save(category);
                    log.info("Created category: {}", name);
                    return saved;
                });
    }

    private Category createCategory(String name, String description) {
        Category category = Category.builder()
                .name(name)
                .description(description)
                .active(true)
                .build();

        log.info("Created category: {}", name);
        return category; // Will be saved with product
    }

    private void createProduct(String name, String description, Category category,
            BigDecimal basePrice, String imageUrl,
            Color[] colors, Size[] sizes, int stockPerVariant) {

        Product product = Product.builder()
                .name(name)
                .description(description)
                .category(category)
                .basePrice(basePrice)
                .imageUrl(imageUrl)
                .active(true)
                .variants(new ArrayList<>())
                .build();

        // Create variants for each color-size combination
        int variantCount = 0;
        for (Color color : colors) {
            for (Size size : sizes) {
                String sku = generateSku(name, color, size);
                ProductVariant variant = ProductVariant.builder()
                        .product(product)
                        .sku(sku)
                        .size(size)
                        .color(color)
                        .price(basePrice) // Same price as base for simplicity
                        .stockQuantity(stockPerVariant)
                        .reservedQuantity(0)
                        .active(true)
                        .build();

                product.getVariants().add(variant);
                variantCount++;
            }
        }

        productRepository.save(product);
        log.info("Created product: {} with {} variants", name, variantCount);
    }

    private String generateSku(String productName, Color color, Size size) {
        // Generate SKU: BASIC-TEE-BLACK-M
        String productCode = productName.toUpperCase()
                .replaceAll("[^A-Z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .substring(0, Math.min(15, productName.length()));

        return String.format("%s-%s-%s", productCode, color.name(), size.name());
    }
}
