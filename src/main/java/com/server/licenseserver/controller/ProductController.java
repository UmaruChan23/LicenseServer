package com.server.licenseserver.controller;

import com.server.licenseserver.entity.Product;
import com.server.licenseserver.exception.model.ProductModel;
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

    @GetMapping("/list")
    public List<ProductModel> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("/add")
    public Product addProduct(@RequestBody String productName) {
          return productService.addProduct(productName);
    }

    @DeleteMapping("/block")
    public boolean blockProduct(@RequestBody Long productId) {
       return productService.blockProduct(productId);
    }
}
