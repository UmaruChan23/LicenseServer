package com.server.licenseserver.service;

import com.server.licenseserver.entity.Product;
import com.server.licenseserver.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    public Product addProduct(String name) {
        Product product = new Product();
        product.setName(name);
        return productRepo.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public boolean blockProduct(Long productId) {
       Product product = productRepo.getById(productId);
       product.setBlocked(true);
       productRepo.save(product);
       return product.isBlocked();
    }
}
