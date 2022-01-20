package com.server.licenseserver.repo;

import com.server.licenseserver.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LicenseRepo extends JpaRepository<License, Long> {
    List<License> findAllByDeviceId (String id);
    License findByCodeAndDeviceId (long codeId, String deviceId);
}
