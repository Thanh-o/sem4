package org.example.testlogic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<CartItem> items = new ArrayList<>();

    public List<CartItem> getItems() {
        return items;
    }

    public void addItem(Product product, int quantity) {
        // Check if product already exists in cart
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        
        // If not, add new item
        items.add(new CartItem(product, quantity));
    }

    public void updateItemQuantity(Long productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                if (quantity <= 0) {
                    items.remove(item);
                } else {
                    item.setQuantity(quantity);
                }
                return;
            }
        }
    }

    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public static class CartItem {
        private Product product;
        private int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getSubtotal() {
            return product.getPrice().multiply(new BigDecimal(quantity));
        }
    }
}
