package com.example.smarthub.services;

import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.models.dtos.RegisterRequest;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {


    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepo, PasswordEncoder encoder, UserRepository userRepository) {
        this.userRepo = userRepo;
        this.encoder  = encoder;
        this.userRepository = userRepository;
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
    public boolean registerUser(RegisterRequest req) {

        // 1. verificăm existența username/email
        if (userRepository.existsByEmail(req.getEmail()) ||
                userRepository.existsByEmail(req.getEmail())) {
            return false; // deja există
        }

        // 2. mapăm DTO -> entitate
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));

        // 3. salvăm
        userRepository.save(user);
        return true;
    }

    public List<User> getPendingUsers() {
        return userRepo.findByEnabledFalse();
    }
}
