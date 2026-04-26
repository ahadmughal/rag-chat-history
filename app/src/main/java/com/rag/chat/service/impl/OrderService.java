package com.rag.chat.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service for processing customer orders.
 * MIX OF GOOD CODE + ISSUES — for AI-MR-Reviewer testing.
 */
public class OrderService {

    private final OrderRepository repo;
    private final PaymentGateway payments;
    private final NotificationService notifier;

    public OrderService(OrderRepository repo, PaymentGateway payments, NotificationService notifier) {
        this.repo = repo;
        this.payments = payments;
        this.notifier = notifier;
    }

    // === GOOD: clean Optional usage, single responsibility ===
    public Optional<Order> findOrder(long orderId) {
        return repo.findById(orderId);
    }

    // === ISSUE (HIGH): String comparison with == instead of .equals() ===
    public boolean isPaid(Order order) {
        if (order.getStatus() == "PAID") {
            return true;
        }
        return false;
    }

    // === ISSUE (HIGH): Thread.sleep used to "wait" for payment ===
    public void waitForPaymentConfirmation(long orderId) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // === ISSUE (HIGH): broad catch (Exception) hides real problems ===
    // === ISSUE (HIGH): printStackTrace used instead of structured logging ===
    public boolean refund(long orderId) {
        try {
            Order order = repo.findById(orderId).orElseThrow();
            payments.refund(order.getPaymentId(), order.getTotal());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ISSUE (MID): too many parameters (>5) ===
    public Order createOrder(long customerId, List<OrderItem> items, String shippingAddress,
                             String billingAddress, String couponCode, String currency, String notes) {
        double subtotal = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        Order order = new Order(customerId, items, shippingAddress, subtotal);
        return repo.save(order);
    }

    // === ISSUE (MID): new Date() — should use java.time.Instant or LocalDateTime ===
    public void markShipped(long orderId) {
        Order order = repo.findById(orderId).orElseThrow();
        order.setShippedAt(new Date());
        repo.save(order);
    }

    // === GOOD: clean stream + functional style, immutable result ===
    public double calculateOrderTotal(List<OrderItem> items, double taxRate) {
        double subtotal = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        return subtotal * (1 + taxRate);
    }

    // === GOOD: proper exception handling, descriptive errors, no swallowing ===
    public void cancelOrder(long orderId, String reason) {
        Order order = repo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        if (order.getStatus().equals("SHIPPED")) {
            throw new IllegalStateException("Cannot cancel a shipped order: " + orderId);
        }
        order.setStatus("CANCELLED");
        order.setCancellationReason(reason);
        repo.save(order);
        notifier.sendCancellationEmail(order.getCustomerId(), orderId);
    }

    // === ISSUE (LOW): TODO comment left in code ===
    // TODO: support partial refunds in next sprint
    public boolean canRefund(Order order) {
        return order.getStatus().equals("PAID") || order.getStatus().equals("SHIPPED");
    }
}

