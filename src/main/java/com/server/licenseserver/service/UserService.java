package com.server.licenseserver.service;

import com.server.licenseserver.entity.Role;
import com.server.licenseserver.entity.User;
import com.server.licenseserver.exception.UserAlreadyExistsException;
import com.server.licenseserver.repo.RoleRepo;
import com.server.licenseserver.repo.TrialRepo;
import com.server.licenseserver.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final TrialRepo trialRepo;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    @Lazy
    public UserService(UserRepo userRepo, RoleRepo roleRepo, TrialRepo trialRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.trialRepo = trialRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User userEntity) throws UserAlreadyExistsException {
        if (findByLogin(userEntity.getLogin()) == null) {
            Role userRole = roleRepo.findByName("ROLE_USER");
            userEntity.setRole(userRole);
            userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
            return userRepo.save(userEntity);
        }
        throw new UserAlreadyExistsException("user already exist");
    }

    public User findByLogin(String login) {
        return userRepo.findByLogin(login);
    }

    public User findByLoginAndPassword(String login, String password) {
        User userEntity = findByLogin(login);
        if (userEntity != null) {
            if (passwordEncoder.matches(password, userEntity.getPassword())) {
                return userEntity;
            }
        }
        return null;
    }

    public boolean hasActivatedLicense(String login) {
        User user = userRepo.findByLogin(login);
        return user.isHasActivatedLicense();
    }

}
