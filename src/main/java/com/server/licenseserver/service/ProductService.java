package com.server.licenseserver.service;

import com.server.licenseserver.entity.Product;
import com.server.licenseserver.exception.model.ProductModel;
import com.server.licenseserver.repo.ProductRepo;
import liquibase.pro.packaged.P;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<ProductModel> getAllProducts() {
        List<ProductModel> models = new ArrayList<>();
        List<Product> products = productRepo.findAll();
        for (Product product : products) {
            models.add(new ProductModel(product));
        }
        return models;
    }

    public boolean blockProduct(Long productId) {
       Product product = productRepo.getById(productId);
       product.setBlocked(true);
       productRepo.save(product);
       return product.isBlocked();
    }
}
