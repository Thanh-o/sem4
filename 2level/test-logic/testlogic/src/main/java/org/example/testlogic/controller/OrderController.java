package org.example.testlogic.controller;

import org.example.testlogic.model.Order;
import org.example.testlogic.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        Optional<Order> order = orderService.getOrderById(id);
        
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            return "order/details";
        } else {
            return "redirect:/";
        }
    }
    
    @GetMapping("/{id}/confirmation")
    public String orderConfirmation(@PathVariable Long id, Model model) {
        Optional<Order> order = orderService.getOrderById(id);
        
        if (order.isPresent()) {
            model.addAttribute("order", order.get());
            return "order/confirmation";
        } else {
            return "redirect:/";
        }
    }
}
