package org.example.testlogic.service;

import org.example.testlogic.model.Cart;
import org.example.testlogic.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {
    
    private final Cart cart = new Cart();
    private final ProductService productService;
    
    @Autowired
    public CartService(ProductService productService) {
        this.productService = productService;
    }
    
    public Cart getCart() {
        return cart;
    }
    
    public void addToCart(Long productId, int quantity) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID: " + productId));
        
        if (!productService.isInStock(productId, quantity)) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }
        
        cart.addItem(product, quantity);
    }
    
    public void updateCartItem(Long productId, int quantity) {
        cart.updateItemQuantity(productId, quantity);
    }
    
    public void removeFromCart(Long productId) {
        cart.removeItem(productId);
    }
    
    public void clearCart() {
        cart.clear();
    }
}
