package org.example.testlogic.controller;

import org.example.testlogic.model.Product;
import org.example.testlogic.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String listProducts(Model model, @RequestParam(required = false) String keyword) {
        List<Product> products;

        if (keyword != null && !keyword.isEmpty()) {
            products = productService.searchProducts(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            products = productService.getAllProducts();
        }

        model.addAttribute("products", products);
        return "product/list";
    }

    @GetMapping("/{id}")
    public String getProductDetails(@PathVariable Long id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product/details";
        } else {
            // Redirect to products page when product not found
            return "redirect:/products";
        }
    }
}
