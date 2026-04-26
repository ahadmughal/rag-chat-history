package com.rag.chat.service.impl;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for processing customer orders. Handles order creation, payment lifecycle,
 * shipping, cancellation, and refunds.
 */
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_SHIPPED = "SHIPPED";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final OrderRepository repo;
    private final PaymentGateway payments;
    private final NotificationService notifier;

    public OrderService(OrderRepository repo, PaymentGateway payments, NotificationService notifier) {
        this.repo = repo;
        this.payments = payments;
        this.notifier = notifier;
    }

    /**
     * Looks up an order by its id.
     *
     * @param orderId the order id
     * @return the order if found, otherwise empty
     */
    public Optional<Order> findOrder(long orderId) {
        return repo.findById(orderId);
    }

    /**
     * Returns true if the order has been paid.
     *
     * @param order the order to check
     * @return true if status is PAID
     */
    public boolean isPaid(Order order) {
        return STATUS_PAID.equals(order.getStatus());
    }

    /**
     * Asynchronously checks payment status after a delay, without blocking the caller thread.
     * Replaces the previous Thread.sleep-based wait with a non-blocking scheduled completion.
     *
     * @param orderId the order id
     * @return a future that completes with true if the order is paid after the delay
     */
    public CompletableFuture<Boolean> waitForPaymentConfirmation(long orderId) {
        return CompletableFuture.supplyAsync(
                () -> isPaid(repo.findById(orderId).orElseThrow()),
                CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS)
        );
    }

    /**
     * Issues a refund for the given order.
     *
     * @param orderId the order to refund
     * @return true if the refund succeeded
     */
    public boolean refund(long orderId) {
        try {
            Order order = repo.findById(orderId).orElseThrow();
            payments.refund(order.getPaymentId(), order.getTotal());
            return true;
        } catch (NoSuchElementException e) {
            log.warn("Refund requested for unknown order {}", orderId);
            return false;
        } catch (PaymentException e) {
            log.error("Payment gateway rejected refund for order {}", orderId, e);
            return false;
        }
    }

    /**
     * Creates a new order for the given customer.
     *
     * @param customerId       the customer placing the order
     * @param items            line items
     * @param shippingAddress  destination address
     * @param billingAddress   billing address
     * @param couponCode       optional coupon code, may be null
     * @param currency         ISO 4217 currency code
     * @param notes            optional customer notes
     * @return the persisted order
     */
    public Order createOrder(long customerId, List<OrderItem> items, String shippingAddress,
                             String billingAddress, String couponCode, String currency, String notes) {
        double subtotal = items.stream().mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();
        Order order = new Order(customerId, items, shippingAddress, subtotal);
        return repo.save(order);
    }

    /**
     * Marks the order as shipped at the current instant.
     *
     * @param orderId the order id
     */
    public void markShipped(long orderId) {
        Order order = repo.findById(orderId).orElseThrow();
        order.setShippedAt(Date.from(Instant.now()));
        repo.save(order);
    }

    /**
     * Computes the order total including tax.
     *
     * @param items   line items
     * @param taxRate tax rate as a decimal (e.g. 0.08 for 8%)
     * @return the total amount
     */
    public double calculateOrderTotal(List<OrderItem> items, double taxRate) {
        double subtotal = items.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        return subtotal * (1 + taxRate);
    }

    /**
     * Cancels the order and notifies the customer.
     *
     * @param orderId the order id
     * @param reason  free-text cancellation reason
     * @throws IllegalArgumentException if the order does not exist
     * @throws IllegalStateException    if the order has already shipped
     */
    public void cancelOrder(long orderId, String reason) {
        Order order = repo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        if (STATUS_SHIPPED.equals(order.getStatus())) {
            throw new IllegalStateException("Cannot cancel a shipped order: " + orderId);
        }
        order.setStatus(STATUS_CANCELLED);
        order.setCancellationReason(reason);
        repo.save(order);
        notifier.sendCancellationEmail(order.getCustomerId(), orderId);
    }

    /**
     * Returns true if the order is in a refundable state.
     *
     * @param order the order to check
     * @return true if the order can be refunded
     */
    public boolean canRefund(Order order) {
        return STATUS_PAID.equals(order.getStatus()) || STATUS_SHIPPED.equals(order.getStatus());
    }
}