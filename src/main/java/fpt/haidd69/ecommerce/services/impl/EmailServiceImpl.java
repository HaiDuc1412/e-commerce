package fpt.haidd69.ecommerce.services.impl;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import fpt.haidd69.ecommerce.entities.Order;
import fpt.haidd69.ecommerce.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Email service implementation for sending transactional emails. Uses async
 * execution to avoid blocking business operations. All email operations run in
 * separate thread pool.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.tracking-path:/api/orders/track/}")
    private String trackingPath;

    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.from-name}")
    private String fromName;

    public EmailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /**
     * Send order confirmation email asynchronously.
     *
     * @param order Order details to include in email
     */
    @Async("emailExecutor")
    @Override
    public void sendOrderConfirmation(Order order) {
        try {
            logger.debug("Starting to send order confirmation email to: {}", order.getCustomerEmail());
            String trackingUrl = baseUrl + trackingPath + order.getTrackingCode();
            String subject = "Xác nhận đơn hàng - " + order.getOrderNumber();

            // Prepare template variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", order.getCustomerName());
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("trackingCode", order.getTrackingCode());
            variables.put("orderDate", order.getCreatedAt());
            variables.put("orderStatus", getStatusText(order.getStatus().name()));
            variables.put("paymentMethod", getPaymentMethodText(order.getPaymentMethod().name()));
            variables.put("totalAmount", order.getTotalAmount());
            variables.put("trackingUrl", trackingUrl);
            variables.put("customerPhone", order.getCustomerPhone());
            variables.put("shippingAddress", order.getShippingAddress());

            // Prepare order items
            variables.put("orderItems", order.getItems().stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("productName", item.getProductVariant().getProduct().getName());
                itemMap.put("size", item.getProductVariant().getSize().name());
                itemMap.put("color", item.getProductVariant().getColor().name());
                itemMap.put("quantity", item.getQuantity());
                itemMap.put("price", item.getPrice());
                itemMap.put("subtotal", item.getSubtotal());
                return itemMap;
            }).toList());

            sendHtmlEmail(order.getCustomerEmail(), subject, "emails/order-confirmation", variables);

            logger.info("Order confirmation email sent to: {} for order: {}",
                    order.getCustomerEmail(), order.getOrderNumber());
        } catch (Exception e) {
            logger.error("Failed to send order confirmation email to: {}. Error: {}",
                    order.getCustomerEmail(), e.getMessage(), e);
        }
    }

    /**
     * Send order status update email asynchronously.
     *
     * @param order Order with updated status
     */
    @Async("emailExecutor")
    @Override
    public void sendOrderStatusUpdate(Order order) {
        try {
            String trackingUrl = baseUrl + trackingPath + order.getTrackingCode();
            String subject = "Cập nhật đơn hàng - " + order.getOrderNumber();

            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", order.getCustomerName());
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("trackingCode", order.getTrackingCode());
            variables.put("orderStatus", getStatusText(order.getStatus().name()));
            variables.put("totalAmount", order.getTotalAmount());
            variables.put("trackingUrl", trackingUrl);

            sendHtmlEmail(order.getCustomerEmail(), subject, "emails/order-status-update", variables);

            logger.info("Order status update email sent to: {} for order: {}",
                    order.getCustomerEmail(), order.getOrderNumber());
        } catch (Exception e) {
            logger.error("Failed to send order status update email to: {}. Error: {}",
                    order.getCustomerEmail(), e.getMessage(), e);
        }
    }

    /**
     * Send payment confirmation email asynchronously.
     *
     * @param order Order with confirmed payment
     */
    @Async("emailExecutor")
    @Override
    public void sendPaymentConfirmation(Order order) {
        try {
            String trackingUrl = baseUrl + trackingPath + order.getTrackingCode();
            String subject = "Xác nhận thanh toán - " + order.getOrderNumber();

            Map<String, Object> variables = new HashMap<>();
            variables.put("customerName", order.getCustomerName());
            variables.put("orderNumber", order.getOrderNumber());
            variables.put("trackingCode", order.getTrackingCode());
            variables.put("orderDate", order.getCreatedAt());
            variables.put("paymentMethod", getPaymentMethodText(order.getPaymentMethod().name()));
            variables.put("totalAmount", order.getTotalAmount());
            variables.put("trackingUrl", trackingUrl);

            sendHtmlEmail(order.getCustomerEmail(), subject, "emails/payment-confirmation", variables);

            logger.info("Payment confirmation email sent to: {} for order: {}",
                    order.getCustomerEmail(), order.getOrderNumber());
        } catch (Exception e) {
            logger.error("Failed to send payment confirmation email to: {}. Error: {}",
                    order.getCustomerEmail(), e.getMessage(), e);
        }
    }

    private void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(variables);

            String html = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            helper.setFrom(fromEmail, fromName);

            mailSender.send(message);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        } catch (Exception e) {
            logger.error("Unexpected error sending email to {}. Error: {}", to, e.getMessage(), e);
            throw new RuntimeException("Unexpected error sending email", e);
        }
    }

    private String getStatusText(String status) {
        return switch (status) {
            case "PENDING_PAYMENT" ->
                "CHỜ THANH TOÁN";
            case "CONFIRMED" ->
                "ĐÃ XÁC NHẬN";
            case "PAID" ->
                "ĐÃ THANH TOÁN";
            case "SHIPPING" ->
                "ĐANG GIAO HÀNG";
            case "DELIVERED" ->
                "ĐÃ GIAO HÀNG";
            case "CANCELLED" ->
                "ĐÃ HỦY";
            default ->
                status;
        };
    }

    private String getPaymentMethodText(String method) {
        return switch (method) {
            case "COD" ->
                "Thanh toán khi nhận hàng (COD)";
            default ->
                method;
        };
    }
}
