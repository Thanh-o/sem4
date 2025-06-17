package org.example.testlogic.controller;

import org.example.testlogic.model.Cart;
import org.example.testlogic.model.Order;
import org.example.testlogic.service.CartService;
import org.example.testlogic.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {
    
    private final CartService cartService;
    private final OrderService orderService;
    
    @Autowired
    public CheckoutController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }
    
    @GetMapping
    public String checkout(Model model) {
        Cart cart = cartService.getCart();
        
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }
        
        model.addAttribute("cart", cart);
        return "checkout/form";
    }
    
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam String customerName,
                           @RequestParam String customerEmail,
                           @RequestParam String shippingAddress,
                           RedirectAttributes redirectAttributes) {
        try {
            Cart cart = cartService.getCart();
            
            if (cart.getItems().isEmpty()) {
                return "redirect:/cart";
            }
            
            Order order = orderService.createOrder(cart, customerName, customerEmail, shippingAddress);
            cartService.clearCart();
            
            redirectAttributes.addFlashAttribute("message", "Order placed successfully");
            return "redirect:/orders/" + order.getId() + "/confirmation";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to place order: " + e.getMessage());
            return "redirect:/checkout";
        }
    }
}
