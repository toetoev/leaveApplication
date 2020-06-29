package com.team2.laps.controller;

import java.net.URI;
import java.util.Collections;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import com.team2.laps.exception.AppException;
import com.team2.laps.model.Role;
import com.team2.laps.model.RoleName;
import com.team2.laps.model.User;
import com.team2.laps.payload.ApiResponse;
import com.team2.laps.payload.JwtAuthenticationResponse;
import com.team2.laps.payload.LoginRequest;
import com.team2.laps.payload.SignUpRequest;
import com.team2.laps.repository.RoleRepository;
import com.team2.laps.repository.UserRepository;
import com.team2.laps.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        UserRepository userRepository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        JwtTokenProvider tokenProvider;

        @PostMapping("/signin")
        public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
                Authentication authentication = authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),
                                                loginRequest.getPassword()));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = tokenProvider.generateToken(authentication);
                RoleName roleName = userRepository.findById(tokenProvider.getUserIdFromJWT(jwt)).get().getRoles().get(0)
                                .getName();
                return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, roleName));
        }

        @PostMapping("/signup")
        @RolesAllowed("ROLE_ADMIN")
        public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
                if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                        return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Username is already taken!"),
                                        HttpStatus.BAD_REQUEST);
                }
                if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                        return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Email Address already in use!"),
                                        HttpStatus.BAD_REQUEST);
                }
                // Creating user's account
                User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
                                signUpRequest.getPassword());
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                Role userRole = roleRepository.findByName(signUpRequest.getRole())
                                .orElseThrow(() -> new AppException("User Role not set."));
                user.setRoles(Collections.singletonList(userRole));
                User result = userRepository.save(user);
                // TODO: understand uri parameters, maybe need to change some of it
                URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/users/{username}")
                                .buildAndExpand(result.getUsername()).toUri();

                return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
        }
}
