package fpt.haidd69.ecommerce.services;

import fpt.haidd69.ecommerce.entities.Order;

public interface EmailService {

    void sendOrderConfirmation(Order order);

    void sendOrderStatusUpdate(Order order);

    void sendPaymentConfirmation(Order order);
}
