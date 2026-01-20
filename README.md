# E-Commerce Backend System
## Tổng Quan
Hệ thống E-commerce Backend (Headless/API-first) được xây dựng cho **Hung Hypebeast Store** - một local brand thời trang Việt Nam.
**Công nghệ sử dụng:**
- **Java 21** - Latest LTS version
- **Spring Boot 4.0.1** - Latest framework
- **PostgreSQL 16** - Production-ready database
- **Spring Security + JWT** - Modern authentication
- **Spring Mail + Thymeleaf** - Email templating
- **MapStruct** - Type-safe object mapping
- **SpringDoc OpenAPI 3.0** - API documentation
- **Caffeine Cache** - High-performance caching
- **Docker** - Containerization support
- **JUnit 5 + Mockito** - Comprehensive unit tests
---
## Tính Năng Đã Hoàn Thành
### 1. Authentication & Authorization
- JWT-based authentication với refresh token support
- Role-based access control (ADMIN, CUSTOMER)
- Secure password hashing (BCrypt)
- User registration với email validation
- Auto-generated default admin & customer accounts
### 2. Product Catalog Management
- Browse products với pagination & sorting
- Product variants (Size: S/M/L/XL, Color: Multiple)
- Advanced filtering (category, price range)
- Product details với variant information
- Stock quantity tracking per variant
- Auto-seeded sample data (10 products, 69 variants)
### 3. Shopping Cart
-  **Session-based cart** - Không cần đăng nhập
-  Add/Update/Remove cart items
-  Real-time stock validation
-  Automatic price calculation
-  Cart persistence với session ID
-  Support multiple variants của cùng 1 product
### 4.  Inventory Management (CRITICAL FEATURE)
- **Inventory Reservation System** - Giữ hàng tự động
  - Tự động reserve stock 10-15 phút khi thêm vào giỏ
  - Pessimistic locking để xử lý race conditions
  - Atomic inventory operations (Thread-safe)
- **Auto-release Expired Reservations**
  - Scheduled job chạy mỗi 5 phút
  - Tự động nhả hàng nếu không checkout
  - Prevent inventory deadlock
- **Reserved Quantity Tracking**
  - Track số lượng đã reserve riêng biệt
  - Available = Stock - Reserved
  - Real-time availability check
### 5. Checkout & Order Management
- Create order từ cart
- Payment methods:
  - **COD (Cash on Delivery)**
  - Bank Transfer (prepared)
  - SePay (prepared)
-  Auto-generated tracking code
-  Customer information capture (name, email, phone, address)
-  Order notes/special requests
-  Order status workflow:
  - PENDING_PAYMENT  CONFIRMED  PAID  SHIPPING  DELIVERED
  - CANCELLED (any time before DELIVERED)
### 6.  Email Notification System
- **3 Professional HTML Templates:**
  - Order Confirmation Email
  - Order Status Update Email
  - Payment Confirmation Email
- Responsive design (mobile-friendly)
- Order tracking link embedded
- Auto-send khi tạo order
- Auto-send khi admin update status
- Thymeleaf template engine
- Vietnamese language support
### 7. Order Tracking
- **Public tracking** - Không cần đăng nhập
- Track bằng tracking code
- Full order details & timeline
- Status history với timestamps
- Customer information display
### 8. Admin Order Management
- View all orders với pagination
- Filter by status (PENDING_PAYMENT, PAID, SHIPPING, etc.)
- View order details
- **Update order status:**
  - CONFIRM - Xác nhận đơn hàng
  - CANCEL - Hủy đơn hàng
-  Auto-send email notification on status change
-  Role-based access (ADMIN only)
-  Inventory release on cancel
### 9.  API Documentation
-  Swagger UI - Interactive API testing
-  OpenAPI 3.0 specification
-  Complete endpoint documentation
-  Request/Response examples
-  Authentication documentation
---
##  Quick Start
### Prerequisites
- Java 21+
- PostgreSQL 13+
- Maven 3.8+
### 1. Clone Repository
```bash
git clone <repository-url>
cd Ecommerce
```
### 2. Database Setup

#### Manual PostgreSQL Setup
```bash
# Tạo database
createdb ecommerce_db
# Hoặc dùng psql
psql -U postgres
CREATE DATABASE ecommerce_db;
```
### 3. Configuration
Copy file `.env.example` thành `.env` và cấu hình:
```env
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=ecommerce_db
DB_USERNAME=postgres
DB_PASSWORD=root
# Server Configuration
SERVER_PORT=8080
# Application Configuration
APP_BASE_URL=http://localhost:8080
# JWT Configuration
# CRITICAL: Must be a valid Base64-encoded string!
# Generate using: openssl rand -base64 64
# Or use online generator: https://generate.plus/en/base64
# Default value is provided for development only
JWT_SECRET=dGhpc2lzYXNlY3VyZWp3dHNlY3JldGtleWZvcmRldmVsb3BtZW50cHVycG9zZXNvbmx5cGxlYXNlY2hhbmdlaW5wcm9kdWN0aW9uZW52aXJvbm1lbnQ=
JWT_EXPIRATION=86400000
# Initial Admin & Customer Passwords
# These are used to create default accounts on first startup
# CRITICAL: Change these immediately after first login in production!
ADMIN_PASSWORD=Admin@123
CUSTOMER_PASSWORD=Customer@123
# Email Configuration (SMTP)
# For Gmail, enable "App Passwords" at: https://myaccount.google.com/apppasswords
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-character-app-password
EMAIL_FROM=noreply@hunghypebeast.com
EMAIL_FROM_NAME=Hung Hypebeast Store
# Logging Level (optional)
LOG_LEVEL=INFO
```
### 4. Build & Run
#### Using Maven
```bash
# Build project
mvn clean install
# Run application
mvn spring-boot:run
```
#### Using Docker
```bash
# Build image
docker build -t ecommerce-backend .
# Run container
docker run -p 8080:8080 --env-file .env ecommerce-backend
```
### 5. Access Application
- **API Base URL:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
---
## API Documentation
### Default Accounts (Auto-created on startup)
**Admin Account:**
```
Email: admin@ecommerce.com
Password: Admin@123
```
**Customer Account:**
```
Email: customer@ecommerce.com
Password: Customer@123
```
### Postman Collection
Import file `Ecommerce_API_Collection.postman_collection.json` vào Postman để test APIs.
**Environment:**
- Import `Ecommerce_API.postman_environment.json`
- Set `baseUrl` = `http://localhost:8080`
### Main API Endpoints
#### Authentication
```http
POST   /api/auth/login              # User login
POST   /api/auth/register           # Customer registration
```
#### Products (Public)
```http
GET    /api/products                       # List all products (paginated)
GET    /api/products/{id}                  # Get product details
GET    /api/products/filter               # Filter by category/price
       ?categoryId={uuid}
       &minPrice={amount}
       &maxPrice={amount}
       &page={0}
       &size={20}
```
#### Shopping Cart (Session-based)
```http
GET    /api/cart                          # Get current cart
POST   /api/cart/items                    # Add item to cart
PUT    /api/cart/items/{itemId}           # Update item quantity
DELETE /api/cart/items/{itemId}           # Remove item from cart
```
#### Orders
```http
POST   /api/orders                        # Create order (checkout)
GET    /api/orders/track/{trackingCode}   # Track order (public, no auth)
```
**Create Order Request:**
```json
{
  "customerName": "Nguyễn Văn A",
  "customerEmail": "customer@example.com",
  "customerPhone": "0901234567",
  "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
  "paymentMethod": "COD",
  "notes": "Giao hàng giờ hành chính"
}
```
#### Admin Order Management (Requires ADMIN role)
```http
GET    /api/admin/orders                  # List all orders
GET    /api/admin/orders?status={status}  # Filter by status
GET    /api/admin/orders/{orderId}        # Get order details
POST   /api/admin/orders/{orderId}/update-status  # Update order status
```
**Update Status Request:**
```json
{
  "action": "CONFIRM"  // or "CANCEL"
}
```
**Order Status Values:**
- `PENDING_PAYMENT` - Chờ xác nhận
- `CONFIRMED` - Đã xác nhận
- `PAID` - Đã thanh toán
- `SHIPPING` - Đang giao hàng
- `DELIVERED` - Đã giao hàng
- `CANCELLED` - Đã hủy
---
## Architecture & Design
### Technology Stack
| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Runtime** | Java 21 | Latest LTS with modern features |
| **Framework** | Spring Boot 4.0.1 | Enterprise application framework |
| **Database** | PostgreSQL 16 | ACID-compliant RDBMS |
| **Security** | Spring Security + JWT | Authentication & Authorization |
| **ORM** | Hibernate 7.2 (JPA) | Database abstraction |
| **Caching** | Caffeine | In-memory high-performance cache |
| **Mapping** | MapStruct 1.5.5 | Type-safe bean mapping |
| **Email** | Spring Mail + Thymeleaf | SMTP with HTML templates |
| **API Docs** | SpringDoc OpenAPI 3.0 | Interactive API documentation |
| **Testing** | JUnit 5 + Mockito | Unit & Integration testing |
| **Build** | Maven 3.8+ | Dependency & build management |
| **Containerization** | Docker | Deployment packaging |
### Database Schema (ERD)

#### Tables Overview

**1. User** - Tài khoản người dùng
```
users
├── id (UUID, PK)
├── email (String, Unique)
├── password (String, BCrypt)
├── name (String)
├── role (Enum: ADMIN, CUSTOMER)
├── active (Boolean)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**2. Category** - Danh mục sản phẩm
```
categories
├── id (UUID, PK)
├── name (String, Unique)
├── description (String)
├── active (Boolean)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**3. Product** - Sản phẩm chính
```
products
├── id (UUID, PK)
├── category_id (UUID, FK → categories)
├── name (String)
├── description (Text)
├── base_price (BigDecimal)
├── image_url (String)
├── active (Boolean)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**4. ProductVariant** - Biến thể sản phẩm (Size/Color)
```
product_variants
├── id (UUID, PK)
├── product_id (UUID, FK → products)
├── sku (String, Unique)
├── size (Enum: S, M, L, XL)
├── color (Enum: BLACK, WHITE, RED, BLUE...)
├── price (BigDecimal)
├── stock_quantity (Integer)
├── reserved_quantity (Integer) Critical for inventory
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**5. InventoryReservation** - Giữ hàng tạm thời
```
inventory_reservations
├── id (UUID, PK)
├── product_variant_id (UUID, FK → product_variants)
├── session_id (String)
├── quantity (Integer)
├── expires_at (Timestamp) 10-15 minutes
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**6. Cart** - Giỏ hàng
```
carts
├── id (UUID, PK)
├── session_id (String, Unique) Guest support
├── user_id (UUID, FK → users, Nullable)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**7. CartItem** - Chi tiết giỏ hàng
```
cart_items
├── id (UUID, PK)
├── cart_id (UUID, FK → carts)
├── product_variant_id (UUID, FK → product_variants)
├── quantity (Integer)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**8. Order** - Đơn hàng
```
orders
├── id (UUID, PK)
├── order_number (String, Unique) e.g., "ORD-20260121-001"
├── tracking_code (String, Unique) e.g., "HHB-ABC123XYZ"
├── user_id (UUID, FK → users, Nullable) Guest support
├── status (Enum: PENDING_PAYMENT, CONFIRMED, PAID...)
├── total_amount (BigDecimal)
├── payment_method (Enum: COD, BANK_TRANSFER, SEPAY)
├── customer_name (String)
├── customer_email (String)
├── customer_phone (String)
├── shipping_address (Text)
├── notes (Text, Nullable)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

**9. OrderItem** - Chi tiết đơn hàng
```
order_items
├── id (UUID, PK)
├── order_id (UUID, FK → orders)
├── product_variant_id (UUID, FK → product_variants)
├── quantity (Integer)
├── price (BigDecimal) Snapshot price at time of order
├── subtotal (BigDecimal)
├── created_at (Timestamp)
└── updated_at (Timestamp)
```

---

#### Relationships

```
Category ──────1:N──────> Product
                             │
                             1:N
                             │
                             ▼
                      ProductVariant ◄───1:N─── InventoryReservation
                             │
                             │ (Referenced by)
                             │
              ┌──────────────┼──────────────┐
              │                             │
              ▼                             ▼
         CartItem                      OrderItem
              │                             │
              │                             │
         Cart (1:N)                    Order (1:N)
              │                             │
              │ (Optional)                  │ (Optional)
              └──────> User ◄───────────────┘
```

#### Key Relationships Explained

1. **Category → Product** (1:N)
   - Một category có nhiều products

2. **Product → ProductVariant** (1:N)
   - Một product có nhiều variants (size/color combinations)

3. **ProductVariant → InventoryReservation** (1:N)
   - Một variant có nhiều reservations (từ nhiều sessions)

4. **Cart → CartItem** (1:N)
   - Một cart chứa nhiều cart items

5. **CartItem → ProductVariant** (N:1)
   - Mỗi cart item reference đến một variant cụ thể

6. **Order → OrderItem** (1:N)
   - Một order chứa nhiều order items

7. **OrderItem → ProductVariant** (N:1)
   - Mỗi order item reference đến một variant (snapshot)

8. **User ← Cart/Order** (1:N, Optional)
   - User có thể có nhiều carts và orders
   - **Nullable** để support guest checkout

#### Important Notes

- **Session-based Cart:** `Cart.session_id` cho phép guest shopping
- **Inventory Reservation:** `reserved_quantity` đảm bảo stock accuracy
- **Price Snapshot:** `OrderItem.price` lưu giá tại thời điểm đặt hàng
- **Tracking Code:** `Order.tracking_code` cho public tracking
- **UUID Primary Keys:** Tất cả tables dùng UUID (better security)
### Key Design Patterns
1. **Layered Architecture**
   ```
   Controller  Service  Repository  Entity
   DTO  Mapper  Service
   ```
2. **Repository Pattern** - Data access abstraction
3. **Service Layer Pattern** - Business logic encapsulation
4. **DTO Pattern** - Data transfer objects for API
5. **Builder Pattern** - Entity construction
6. **Strategy Pattern** - Payment methods
7. **Template Method** - Email templates
### Core Features Implementation
#### Inventory Reservation System
```java
@Transactional
public void reserveInventory(String sessionId, UUID variantId, Integer quantity) {
    // 1. Pessimistic lock - prevent race conditions
    ProductVariant variant = variantRepository.findByIdWithLock(variantId);
    // 2. Check available stock
    Integer available = variant.getStockQuantity() - variant.getReservedQuantity();
    if (available < quantity) {
        throw new InsufficientStockException();
    }
    // 3. Create reservation (expires in 10-15 min)
    InventoryReservation reservation = InventoryReservation.builder()
        .sessionId(sessionId)
        .productVariant(variant)
        .quantity(quantity)
        .expiresAt(LocalDateTime.now().plusMinutes(10))
        .build();
    reservationRepository.save(reservation);
    // 4. Update reserved quantity (atomic)
    variant.setReservedQuantity(variant.getReservedQuantity() + quantity);
    variantRepository.save(variant);
}
// Auto-release expired reservations every 5 minutes
@Scheduled(fixedRate = 300000)
@Transactional
public void releaseExpiredReservations() {
    LocalDateTime now = LocalDateTime.now();
    List<InventoryReservation> expired =
        reservationRepository.findExpiredReservations(now);
    for (InventoryReservation reservation : expired) {
        ProductVariant variant = reservation.getProductVariant();
        variant.setReservedQuantity(
            variant.getReservedQuantity() - reservation.getQuantity()
        );
        variantRepository.save(variant);
    }
    reservationRepository.deleteExpiredReservations(now);
}
```
#### Email Notification Flow
```
Order Created  EmailService.sendOrderConfirmation()
            Thymeleaf renders HTML template
            SMTP sends email
            Customer receives email with tracking link
Admin Updates Status  EmailService.sendOrderStatusUpdate()
                    Customer receives update email
```
### Security Implementation
**JWT Authentication Flow:**
```
1. User Login  POST /api/auth/login
2. Backend validates credentials
3. Generate JWT token (24h expiry)
4. Return token + user info
5. Client stores token
6. Subsequent requests: Authorization: Bearer {token}
7. JwtFilter validates token
8. SecurityContext populated with user details
9. @PreAuthorize checks role
```
**Password Security:**
- BCrypt hashing (strength 10)
- Salt generated per password
- No plain text storage
---
## Email System
Hệ thống email sử dụng **Thymeleaf HTML templates** với responsive design.
### Available Templates
| Template | Trigger | Content |
|----------|---------|---------|
| `order-confirmation.html` | Order created | Order details, items, tracking link |
| `order-status-update.html` | Admin updates status | Status change notification |
| `payment-confirmation.html` | Payment confirmed | Payment receipt, order summary |
### Email Features
- Responsive HTML design (mobile-friendly)
- Vietnamese language support
- Branded with store information
- Clickable tracking links
- Order item details with images
- Professional styling
### Configuration
**Quick Gmail Setup:**
1. Tạo Gmail App Password:
   - Truy cập: https://myaccount.google.com/apppasswords
   - Tạo app password mới
   - Copy 16-character password
2. Update `.env` hoặc `application.properties`:
```properties
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-16-char-app-password
EMAIL_FROM=noreply@hunghypebeast.com
EMAIL_FROM_NAME=Hung Hypebeast Store
```
3. Restart application
4. Test bằng cách tạo order mới
---
### Manual Testing với Postman
**Import Collections:**
1. `Ecommerce.postman_collection.json` - API endpoints
2. `Ecommerce.postman_environment.json` - Environment variables
**Test Flow:**
**Scenario 1: Guest Shopping Flow**
```
1. Browse Products
   GET /api/products
2. View Product Details
   GET /api/products/{id}
3. Add to Cart (generate Session-Id first)
   POST /api/cart/items
   Header: Session-Id: {uuid}
4. View Cart
   GET /api/cart
   Header: Session-Id: {same-uuid}
5. Checkout
   POST /api/orders
   Header: Session-Id: {same-uuid}
6. Check Email (order confirmation)
7. Track Order (no auth needed)
   GET /api/orders/track/{trackingCode}
```
**Scenario 2: Admin Management Flow**
```
1. Admin Login
   POST /api/auth/login
   Body: { "email": "admin@ecommerce.com", "password": "Admin@123" }
2. Get All Orders
   GET /api/admin/orders
   Header: Authorization: Bearer {token}
3. Filter Orders by Status
   GET /api/admin/orders?status=PENDING_PAYMENT
4. Update Order Status
   POST /api/admin/orders/{orderId}/update-status
   Header: Authorization: Bearer {token}
          Session-Id: {session-id}
   Body: { "action": "CONFIRM" }
5. Customer receives email notification
```
**Scenario 3: Customer Registration Flow**
```
1. Register
   POST /api/auth/register
   Body: {
     "email": "newcustomer@example.com",
     "password": "Password123!",
     "name": "Nguyễn Văn B"
   }
2. Login
   POST /api/auth/login
3. (Future) View Order History
```
---
## Configuration Files
### application.properties
Chứa tất cả cấu hình Spring Boot:
- Database connection
- JPA/Hibernate settings
- Email (SMTP) configuration
- JWT settings
- Swagger/OpenAPI config
- Actuator endpoints
- Scheduling config
### docker-compose.yml
Setup PostgreSQL database với Docker:
```yaml
services:
  postgres:
    image: postgres:16-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_DB: ecommerce_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
```
---
## Troubleshooting
### Database Connection Error
```
Caused by: org.postgresql.util.PSQLException: Connection refused
```
**Solution:**
- Kiểm tra PostgreSQL đang chạy: `pg_isready`
- Kiểm tra port: `netstat -an | grep 5432`
- Kiểm tra credentials trong `.env`
### Email Not Sending
```
Failed to send email. Error: Authentication failed
```
**Solution:**
- Xem [EMAIL_SETUP_GUIDE.md](./EMAIL_SETUP_GUIDE.md)
- Kiểm tra App Password (Gmail)
- Kiểm tra SMTP credentials
- Enable debug: `logging.level.org.springframework.mail=DEBUG`
### Inventory Issues
```
InsufficientStockException: Insufficient stock
```
**Solution:**
- Đảm bảo có data seed (DataInitializer tự động chạy)
- Kiểm tra `reservedQuantity` trong database
- Clear expired reservations: Scheduled job chạy mỗi 5 phút

## API Usage Examples
### 1. Product Browsing
```bash
# Get all products with pagination
curl -X GET "http://localhost:8080/api/products?page=0&size=20&sortBy=createdAt"
# Filter products by category and price
curl -X GET "http://localhost:8080/api/products/filter?categoryId={uuid}&minPrice=100000&maxPrice=500000"
# Get product details
curl -X GET "http://localhost:8080/api/products/{productId}"
```
### 2. Shopping Cart Operations
```bash
# Generate a session ID first (can be any UUID)
SESSION_ID="550e8400-e29b-41d4-a716-446655440000"
# Get cart
curl -X GET http://localhost:8080/api/cart \
  -H "Session-Id: $SESSION_ID"
# Add to cart
curl -X POST http://localhost:8080/api/cart/items \
  -H "Session-Id: $SESSION_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "productVariantId": "variant-uuid-here",
    "quantity": 2
  }'
# Update cart item quantity
curl -X PUT http://localhost:8080/api/cart/items/{itemId}?quantity=3 \
  -H "Session-Id: $SESSION_ID"
# Remove from cart
curl -X DELETE http://localhost:8080/api/cart/items/{itemId} \
  -H "Session-Id: $SESSION_ID"
```
### 3. Checkout & Create Order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Session-Id: $SESSION_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Nguyễn Văn A",
    "customerEmail": "customer@example.com",
    "customerPhone": "0901234567",
    "shippingAddress": "123 Đường ABC, Quận 1, TP.HCM",
    "paymentMethod": "COD",
    "notes": "Giao hàng giờ hành chính"
  }'
```
**Response:**
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": "order-uuid",
    "orderNumber": "ORD-20260121-001",
    "trackingCode": "HHB-ABC123XYZ",
    "status": "PENDING_PAYMENT",
    "totalAmount": 1500000,
    "items": [...],
    "createdAt": "2026-01-21T10:30:00"
  }
}
```
### 4. Track Order (No Auth)
```bash
# Anyone can track with tracking code
curl -X GET http://localhost:8080/api/orders/track/HHB-ABC123XYZ
```
### 5. Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@ecommerce.com",
    "password": "Admin@123"
  }'
```
**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "email": "admin@ecommerce.com",
    "role": "ADMIN",
    "expiresIn": 86400000
  }
}
```
### 6. Admin - View All Orders
```bash
TOKEN="your-admin-jwt-token"
# Get all orders
curl -X GET http://localhost:8080/api/admin/orders \
  -H "Authorization: Bearer $TOKEN"
# Filter by status
curl -X GET "http://localhost:8080/api/admin/orders?status=PENDING_PAYMENT&page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
# Get specific order
curl -X GET http://localhost:8080/api/admin/orders/{orderId} \
  -H "Authorization: Bearer $TOKEN"
```
### 7. Admin - Update Order Status
```bash
# Confirm payment
curl -X POST http://localhost:8080/api/admin/orders/{orderId}/update-status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Session-Id: admin-session-id" \
  -H "Content-Type: application/json" \
  -d '{
    "action": "CONFIRM"
  }'
# Cancel order
curl -X POST http://localhost:8080/api/admin/orders/{orderId}/update-status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Session-Id: admin-session-id" \
  -H "Content-Type: application/json" \
  -d '{
    "action": "CANCEL"
  }'
```
**Note:** Customer sẽ tự động nhận email khi admin update status.
---
## Project Structure

### Tổng Quan Cấu Trúc
```
Ecommerce/
├── src/
│   ├── main/
│   │   ├── java/fpt/haidd69/ecommerce/     # Source code chính
│   │   └── resources/                       # Resources & configs
│   └── test/                                # Unit tests
├── target/                                  # Build output (auto-generated)
├── docker-compose.yml                       # PostgreSQL container setup
├── Dockerfile                               # App containerization
├── pom.xml                                  # Maven dependencies
├── .env                                     # Environment variables (gitignored)
├── README.md                                # Documentation
├── TECHNICAL_REPORT.md                      # Technical details
├── Ecommerce.postman_collection.json        # API testing
└── Ecommerce.postman_environment.json       # Postman environment
```

### Java Source Code Structure (`src/main/java/fpt/haidd69/ecommerce/`)

#### 1️. **config/** - Cấu hình ứng dụng
```
config/
├── AsyncConfig.java          # Async processing configuration
├── CacheConfig.java          # Caffeine cache configuration
├── CorsConfig.java           # Cross-Origin Resource Sharing
├── DataInitializer.java      # Auto-seed sample data on startup
├── JwtFilter.java            # JWT authentication filter
├── OpenAPIConfig.java        # Swagger/OpenAPI documentation
└── SecurityConfig.java       # Spring Security configuration
```

#### 2️. **constants/** - Hằng số & thông báo lỗi
```
constants/
├── AppConstants.java         # Application-wide constants
└── ErrorCodes.java           # Error codes & messages
```

#### 3️. **controllers/** - REST API Endpoints
```
controllers/
├── AdminController.java      # Admin order management APIs
├── AuthController.java       # Login/Register/Logout
├── CartController.java       # Shopping cart operations
├── OrderController.java      # Order creation & tracking
└── ProductController.java    # Product catalog browsing
```

#### 4️. **dto/** - Data Transfer Objects (Request/Response)
```
dto/
├── auth/                     # Authentication DTOs
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── RegisterRequest.java
├── cart/                     # Shopping cart DTOs
│   ├── AddToCartRequest.java
│   ├── CartResponse.java
│   └── UpdateCartItemRequest.java
├── common/                   # Shared DTOs
│   ├── ApiResponse.java
│   └── PagedResponse.java
├── order/                    # Order DTOs
│   ├── CreateOrderRequest.java
│   ├── OrderResponse.java
│   └── UpdateOrderRequest.java
└── product/                  # Product DTOs
    ├── ProductResponse.java
    ├── ProductDetailResponse.java
    └── ProductVariantResponse.java
```

#### 5️. **entities/** - JPA Database Entities
```
entities/
├── BaseEntity.java           # Base class (id, createdAt, updatedAt)
├── Cart.java                 # Shopping cart
├── CartItem.java             # Cart line items
├── Category.java             # Product categories
├── InventoryReservation.java # Stock reservation tracking
├── Order.java                # Customer orders
├── OrderItem.java            # Order line items
├── Product.java              # Product master data
├── ProductVariant.java       # Size/Color variations
└── User.java                 # User accounts
```

#### 6️. **enums/** - Type-safe Constants
```
enums/
├── Color.java                # Product colors (BLACK, WHITE, RED, etc.)
├── OrderStatus.java          # Order lifecycle states
├── PaymentMethod.java        # COD, BANK_TRANSFER, SEPAY
├── Role.java                 # ADMIN, CUSTOMER
└── Size.java                 # S, M, L, XL
```

#### 7️. **exceptions/** - Error Handling
```
exceptions/
├── GlobalExceptionHandler.java        # Centralized error handler
├── InsufficientStockException.java    # Out of stock error
├── InvalidOrderStatusException.java   # Invalid status transition
└── ResourceNotFoundException.java     # 404 errors
```

#### 8️. **mappers/** - Entity ↔ DTO Mapping
```
mappers/
├── CartMapper.java           # Cart entity ↔ CartResponse
├── OrderMapper.java          # Order entity ↔ OrderResponse
├── ProductMapper.java        # Product entity ↔ ProductResponse
└── UserMapper.java           # User entity ↔ UserResponse
```
*Sử dụng MapStruct - compile-time code generation*

#### 9. **repositories/** - Data Access Layer
```
repositories/
├── CartRepository.java
├── CategoryRepository.java
├── InventoryReservationRepository.java
├── OrderRepository.java
├── ProductRepository.java
├── ProductVariantRepository.java
└── UserRepository.java
```
*Spring Data JPA - auto-implementation của CRUD operations*

#### 10. **services/** - Business Logic Layer
```
services/
├── impl/                     # Service implementations
│   ├── AuthServiceImpl.java
│   ├── CartServiceImpl.java
│   ├── CustomUserDetailsService.java
│   ├── EmailServiceImpl.java
│   ├── InventoryServiceImpl.java
│   ├── OrderServiceImpl.java
│   ├── ProductServiceImpl.java
│   └── TokenServiceImpl.java
├── AuthService.java          # Authentication & registration
├── CartService.java          # Shopping cart operations
├── EmailService.java         # Email notifications
├── InventoryService.java     # Stock reservation & release
├── OrderService.java         # Order processing
├── ProductService.java       # Product catalog
└── TokenService.java         # JWT token management
```

### Resources Structure (`src/main/resources/`)
```
resources/
├── templates/
│   └── emails/               # Thymeleaf HTML email templates
│       ├── order-confirmation.html
│       ├── order-status-update.html
│       └── payment-confirmation.html
└── application.properties    # Spring Boot configuration
---

### Package Organization Summary
| Package | Purpose |
|---------|---------|
| `config` | Spring configuration, Security, JWT, CORS, OpenAPI |
| `constants` | Application-wide constants and messages |
| `controllers` | REST API endpoints (thin layer) |
| `dto` | Request/Response objects for API |
| `entities` | JPA entities (database models) |
| `enums` | Type-safe constants |
| `exceptions` | Custom exceptions & global handler |
| `mappers` | Entity  DTO mapping (MapStruct) |
| `repositories` | Data access layer (Spring Data JPA) |
| `services` | Business logic (thick layer) |
---
## Future Enhancements (Phase 2)
### Planned Features
#### E-Commerce Features
- [ ] **Product Management Admin API**
  - CRUD operations for products
  - Bulk upload products via CSV/Excel
  - Product image upload & management
  - Category management
  - Variant management (dynamic size/color)
- [ ] **Advanced Search & Filtering**
  - Full-text search (product name, description)
  - Multi-criteria filtering
  - Sort by popularity, price, newest
  - Search suggestions/autocomplete
- [ ] **Customer Features**
  - Order history for authenticated users
  - Wishlist/Favorites
  - Product reviews & ratings
  - Customer profile management
- [ ] **Promotions & Marketing**
  - Discount codes & coupons
  - Flash sales / Limited time offers
  - Buy X Get Y promotions
  - Free shipping threshold
#### Payment Integration
- [ ] **SePay Webhook Integration**
  - Real-time payment notifications
  - Auto-update order status on payment
  - Payment reconciliation
- [ ] **Bank Transfer Support**
  - Bank account information display
  - Manual payment confirmation
#### Analytics & Reports
- [ ] **Admin Dashboard**
  - Sales statistics (daily/monthly)
  - Revenue reports
  - Best-selling products
  - Customer analytics
  - Inventory alerts (low stock)
- [ ] **Export Reports**
  - Order reports (Excel/CSV)
  - Revenue reports
  - Inventory reports
#### Shipping & Logistics
- [ ] **Shipping Cost Calculation**
  - Integration with shipping providers (GHN, GHTK, VNPost)
  - Distance-based pricing
  - Weight-based pricing
- [ ] **Shipping Tracking**
  - Real-time tracking updates
  - Webhook from shipping providers
  - SMS notifications
#### System Improvements
- [ ] **Performance Optimization**
  - Redis for distributed caching
  - Database query optimization
  - CDN for static assets
- [ ] **Monitoring & Logging**
  - Centralized logging (ELK stack)
  - Application metrics (Prometheus + Grafana)
  - Error tracking (Sentry)
- [ ] **DevOps**
  - CI/CD pipeline (GitHub Actions / Jenkins)
  - Kubernetes deployment
  - Auto-scaling configuration
  - Database backup automation
####  Mobile Support
- [ ] RESTful API versioning
- [ ] Push notifications
- [ ] Mobile-optimized responses
---
##  Security Considerations
### Current Implementation
 **Implemented:**
- JWT token authentication
- BCrypt password hashing
- Role-based access control (RBAC)
- CORS configuration
- Input validation
- SQL injection prevention (JPA/Hibernate)
- XSS protection (Spring Security defaults)
### Production Recommendations
 **Before Going Live:**
1. **Environment Variables**
   - Move all secrets to environment variables
   - Use secrets management (AWS Secrets Manager, Azure Key Vault)
   - Never commit `.env` to Git
2. **HTTPS Only**
   - Enforce HTTPS in production
   - Set `server.ssl.enabled=true`
   - Obtain SSL certificate (Let's Encrypt)
3. **Database Security**
   - Use connection pooling (HikariCP - already configured)
   - Limit database user permissions
   - Enable SSL for database connections
   - Regular backups with encryption
4. **Rate Limiting**
   - Implement rate limiting per IP
   - API throttling for public endpoints
   - DDoS protection (Cloudflare)
5. **Monitoring & Alerting**
   - Set up error monitoring
   - Failed login attempt tracking
   - Unusual activity detection
6. **Data Privacy (GDPR/PDPA)**
   - Customer data encryption at rest
   - Right to be forgotten implementation
   - Privacy policy compliance
---
### Optimization Strategies
1. **Database**
   - Indexed columns: email, orderNumber, trackingCode, sessionId
   - Query optimization with EXPLAIN ANALYZE
   - Connection pooling (HikariCP)
   - JPA batch operations
2. **Caching**
   - Caffeine cache for products catalog
   - Category caching
   - Cache invalidation strategy
3. **Application**
   - Async email sending
   - Scheduled job for cleanup
   - Lazy loading for relationships
---
## Contributors
- **Developer:** Dao Duc Hai
---
## Support
For issues and questions:
- **Email:** HaiDDHE176390@fpt.edu.vn
- **API Docs:** http://localhost:8080/swagger-ui.html
---
