package com.server.licenseserver.repo;

import com.server.licenseserver.entity.Trial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrialRepo extends JpaRepository<Trial, Long> {
    Trial findByUserIdAndDeviceId(long userId, String deviceId);
}
