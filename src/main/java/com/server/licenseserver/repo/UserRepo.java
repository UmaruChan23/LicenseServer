package com.server.licenseserver.repo;

import com.server.licenseserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByLogin(String login);
}
