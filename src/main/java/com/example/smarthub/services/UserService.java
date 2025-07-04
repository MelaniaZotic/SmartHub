package com.example.smarthub.services;

import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.models.dtos.RegisterRequest;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {


    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepo, PasswordEncoder encoder, UserRepository userRepository) {
        this.userRepo = userRepo;
        this.encoder  = encoder;
        this.userRepository = userRepository;
    }


    public void registerUser(String username,String rawPw) {
        logger.info("Registering new user with username: {}", username);
        User u = new User();
        //u.setUsername(username);
        u.setPassword(encoder.encode(rawPw));
        u.setRoles(Set.of(Role.STUDENT));
        u.setEnabled(false);
        userRepo.save(u);
        logger.debug("User saved in repository");
    }

    /* aprobare cont de către admin */
    public void approveUser(Long id) {
        logger.info("Approving user with id: {}", id);
        userRepo.findById(id).ifPresent(u -> {
            u.setEnabled(true);
            userRepo.save(u);
            logger.debug("User with id {} enabled", id);
        });
    }

    public boolean registerUser(RegisterRequest req) {
        logger.info("Attempting registration for email: {}", req.getEmail());
        if (userRepository.existsByEmail(req.getEmail())) {
            logger.warn("Email already exists: {}", req.getEmail());
            return false;
        }

        String email = req.getEmail().trim().toLowerCase();

        Role role;
        if (email.endsWith("@s.univ.ro")) {
            role = Role.STUDENT;
        } else if (email.endsWith("@prof.univ.ro")) {
            role = Role.PROFESSOR;
        } else {
            return false; // invalid
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(req.getPassword()));
        user.getRoles().add(role);

        userRepository.save(user);
        logger.info("User successfully registered with role: {}", role);
        return true;
    }


    public List<User> getPendingUsers() {

        logger.debug("Fetching pending users");
        return userRepo.findByEnabledFalse();
    }
}
