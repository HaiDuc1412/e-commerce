package fpt.haidd69.ecommerce.constants;

public final class ErrorCodes {

    public static final String INTERNAL_SERVER_ERROR = "ERR_1000";
    public static final String VALIDATION_ERROR = "ERR_1001";
    public static final String INVALID_REQUEST = "ERR_1002";

    public static final String UNAUTHORIZED = "ERR_2000";
    public static final String INVALID_CREDENTIALS = "ERR_2001";
    public static final String TOKEN_EXPIRED = "ERR_2002";
    public static final String TOKEN_INVALID = "ERR_2003";
    public static final String ACCESS_DENIED = "ERR_2004";

    public static final String RESOURCE_NOT_FOUND = "ERR_3000";
    public static final String USER_NOT_FOUND = "ERR_3001";
    public static final String PRODUCT_NOT_FOUND = "ERR_3002";
    public static final String ORDER_NOT_FOUND = "ERR_3003";
    public static final String CART_NOT_FOUND = "ERR_3004";

    public static final String INSUFFICIENT_STOCK = "ERR_4000";
    public static final String INVALID_ORDER_STATUS = "ERR_4001";
    public static final String CART_EMPTY = "ERR_4002";
    public static final String DUPLICATE_EMAIL = "ERR_4003";
    public static final String INVALID_PAYMENT_METHOD = "ERR_4004";

    private ErrorCodes() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
