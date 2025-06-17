package org.example.testlogic.service;

import org.example.testlogic.model.Cart;
import org.example.testlogic.model.Order;
import org.example.testlogic.model.OrderItem;
import org.example.testlogic.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductService productService;
    
    @Autowired
    public OrderService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }
    
    @Transactional
    public Order createOrder(Cart cart, String customerName, String customerEmail, String shippingAddress) {
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order with an empty cart");
        }
        
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setShippingAddress(shippingAddress);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (Cart.CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem(cartItem.getProduct(), cartItem.getQuantity());
            order.addItem(orderItem);
            total = total.add(orderItem.getSubtotal());
            
            // Update product stock
            productService.updateStock(cartItem.getProduct().getId(), cartItem.getQuantity());
        }
        
        order.setTotalAmount(total);
        return orderRepository.save(order);
    }
    
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID: " + orderId));
        
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
