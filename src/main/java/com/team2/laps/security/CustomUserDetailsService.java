package com.team2.laps.security;

import javax.transaction.Transactional;

import com.team2.laps.exception.ResourceNotFoundException;
import com.team2.laps.model.User;
import com.team2.laps.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String nameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByNameOrEmail(nameOrEmail, nameOrEmail).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username or email : " + nameOrEmail));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserPrincipal.create(user);
    }
}