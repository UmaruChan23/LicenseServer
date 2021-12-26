package com.server.licenseserver.controller;

import com.server.licenseserver.entity.Product;
import com.server.licenseserver.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping
    public Product addProduct(@RequestBody String productName) {
          return productService.addProduct(productName);
    }

    @DeleteMapping
    public boolean blockProduct(@RequestBody Long productId) {
       return productService.blockProduct(productId);
    }
}
