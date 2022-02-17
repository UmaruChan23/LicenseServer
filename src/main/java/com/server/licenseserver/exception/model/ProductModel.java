package com.server.licenseserver.exception.model;

import com.server.licenseserver.entity.Product;
import lombok.Data;

@Data
public class ProductModel {
    private String name;

    public ProductModel(Product product) {
        this.name = product.getName();
    }
}
