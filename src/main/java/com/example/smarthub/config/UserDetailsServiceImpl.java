package com.example.smarthub.config;

import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repo;
    public UserDetailsServiceImpl(UserRepository repo){ this.repo = repo;}

    @Override
    public UserDetails loadUserByUsername(String email) {
        User u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));
        if (!u.isEnabled())
            throw new DisabledException("Account not approved");

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail()) // ← identificatorul va fi email
                .password(u.getPassword())
                .roles(u.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();
    }

}