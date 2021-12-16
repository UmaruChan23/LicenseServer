package com.server.licenseserver.repo;

import com.server.licenseserver.entity.ActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivationCodeRepo extends JpaRepository<ActivationCode, Long> {
    ActivationCode findByCode(String code);
}
