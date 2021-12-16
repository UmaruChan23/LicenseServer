package com.server.licenseserver.repo;

import com.server.licenseserver.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepo extends JpaRepository<License, Long> {
    License findByCode(long code);
}
