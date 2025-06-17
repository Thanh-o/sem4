package org.example.testlogic.controller;

import org.example.testlogic.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {
    
    private final CartService cartService;
    
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    @GetMapping
    public String viewCart(Model model) {
        model.addAttribute("cart", cartService.getCart());
        return "cart/view";
    }
    
    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId, 
                           @RequestParam(defaultValue = "1") int quantity,
                           RedirectAttributes redirectAttributes) {
        try {
            cartService.addToCart(productId, quantity);
            redirectAttributes.addFlashAttribute("message", "Product added to cart successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/products/" + productId;
    }
    
    @PostMapping("/update/{productId}")
    public String updateCartItem(@PathVariable Long productId,
                                @RequestParam int quantity,
                                RedirectAttributes redirectAttributes) {
        try {
            cartService.updateCartItem(productId, quantity);
            redirectAttributes.addFlashAttribute("message", "Cart updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/cart";
    }
    
    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId,
                               RedirectAttributes redirectAttributes) {
        cartService.removeFromCart(productId);
        redirectAttributes.addFlashAttribute("message", "Item removed from cart");
        return "redirect:/cart";
    }
    
    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        cartService.clearCart();
        redirectAttributes.addFlashAttribute("message", "Cart cleared");
        return "redirect:/cart";
    }
}
