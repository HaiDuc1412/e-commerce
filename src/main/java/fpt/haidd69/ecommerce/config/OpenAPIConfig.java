package fpt.haidd69.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * OpenAPI (Swagger) configuration.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "E-commerce API",
                version = "1.0.0",
                description = """
                ## E-Commerce Backend REST API
                
                Backend API cho hệ thống e-commerce "Local Brand Fashion Store".
                
                ### Features
                - Product Catalog với filter và pagination
                - Shopping Cart management (session-based)
                - Inventory Management với reservation system
                - Order Checkout (COD payment)
                - Order Tracking (không cần login)
                - Admin APIs cho order management
                
                ### Authentication
                - Most endpoints are public (Product, Cart, Order Tracking)
                - Admin endpoints require JWT Bearer token
                - Use `/api/auth/login` to get token
        
                """,
                contact = @Contact(
                        name = "HaiDD69"
                )
        ),
        servers = {
            @Server(url = "http://localhost:8080", description = "Development Server")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER,
        description = """
            Enter your JWT token. 
            
            To get token:
            1. Register user via `/api/auth/register`
            2. Login via `/api/auth/login`
            3. Copy the `token` from response
            4. Click 'Authorize' button above
            5. Enter token (without 'Bearer ' prefix)
            
            The token will be sent in Authorization header as: Bearer {token}
            """
)
public class OpenAPIConfig {
}
