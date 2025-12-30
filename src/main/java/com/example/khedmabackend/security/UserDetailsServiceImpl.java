package com.example.khedmabackend.security;

import com.example.khedmabackend.model.User;
import com.example.khedmabackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService; // ✅ Fix : Utilise UserService au lieu UserRepository direct

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // ✅ Fix : Gère Optional<User> safe (orElseThrow, pas incompatible types)
        Optional<User> userOptional = userService.findByEmail(email);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getType())
                .build();
    }
}