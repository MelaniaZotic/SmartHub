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
    public UserDetails loadUserByUsername(String username) {
        User u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        if(!u.isEnabled())
            throw new DisabledException("Account not approved");
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRoles().stream().map(Enum::name).toArray(String[]::new))
                .build();
    }
}