package com.server.licenseserver.repo;

import com.server.licenseserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Long> {
    Product findById(long id);
}
