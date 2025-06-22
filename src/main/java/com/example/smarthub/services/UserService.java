package com.example.smarthub.services;

import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
public class UserService {


    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder  = encoder;
    }

    /* register */
    public void registerUser(String username,String rawPw) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(encoder.encode(rawPw));
        u.setRoles(Set.of(Role.STUDENT));
        u.setEnabled(false);
        userRepo.save(u);
    }

    /* aprobare cont de către admin */
    public void approveUser(Long id) {
        userRepo.findById(id).ifPresent(u -> {
            u.setEnabled(true);
            userRepo.save(u);
        });
    }

    public List<User> getPendingUsers() {
        return userRepo.findByEnabledFalse();
    }
}
