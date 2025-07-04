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


    public void registerUser(String username,String rawPw) {
        User u = new User();
        //u.setUsername(username);
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
        if (userRepository.existsByEmail(req.getEmail())) {
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
        return true;
    }


    public List<User> getPendingUsers() {
        return userRepo.findByEnabledFalse();
    }
}
