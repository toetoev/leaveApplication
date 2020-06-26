package com.team2.laps.controller;

import com.team2.laps.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

        @Autowired
        UserRepository userRepository;

        @GetMapping("/index")
        public String index() {
                return "auth/auth";
        }

        // @PostMapping("/signin")
        // public String authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
        // {
        // return "home";
        // }

        // @PostMapping("/signup")
        // public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest
        // signUpRequest) {
        // if (userRepository.existsByUsername(signUpRequest.getUsername())) {
        // return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Username is
        // already taken!"),
        // HttpStatus.BAD_REQUEST);
        // }
        // if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        // return new ResponseEntity<ApiResponse>(new ApiResponse(false, "Email Address
        // already in use!"),
        // HttpStatus.BAD_REQUEST);
        // }
        // // Creating user's account
        // User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
        // signUpRequest.getEmail(),
        // signUpRequest.getPassword());
        // user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Role userRole = roleRepository.findByName(signUpRequest.getRole())
        // .orElseThrow(() -> new AppException("User Role not set."));
        // user.setRoles(Collections.singleton(userRole));
        // User result = userRepository.save(user);
        // // TODO: understand uri parameters, maybe need to change some of it

        // return "home";
        // }
}
