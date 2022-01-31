package com.server.licenseserver.repo;

import com.server.licenseserver.entity.ActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;

public interface ActivationCodeRepo extends JpaRepository<ActivationCode, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ActivationCode findByCode(String code);
}
