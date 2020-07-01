package com.team2.laps.controller;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import com.team2.laps.model.RoleName;
import com.team2.laps.model.User;
import com.team2.laps.payload.ApiResponse;
import com.team2.laps.payload.LoginRequest;
import com.team2.laps.payload.SignUpRequest;
import com.team2.laps.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
	@Autowired
	private UserService userService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.signInUser(loginRequest));
	}

	@PostMapping("/signup")
	@RolesAllowed("ROLE_ADMIN")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		return ResponseEntity.ok(userService.registerUser(signUpRequest));
	}

	@GetMapping("/{role}")
	@RolesAllowed("ROLE_ADMIN")
	public ResponseEntity<?> getAllManagers(@PathVariable RoleName role) {
		return ResponseEntity.ok(new ApiResponse(userService.getAll(role)));
	}

	@GetMapping
	@RolesAllowed("ROLE_ADMIN")
	public ResponseEntity<?> getAllUsers() {
		return ResponseEntity.ok(new ApiResponse(userService.getAll(null)));
	}

	@DeleteMapping("/{id}")
	@RolesAllowed("ROLE_ADMIN")
	public void deleteUser(@PathVariable String id) {
		userService.deleteUser(id);
	}

	@PutMapping("/{id}")
	@RolesAllowed("ROLE_ADMIN")
	public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
		return ResponseEntity.ok(userService.updateUser(id, user));
	}

}
