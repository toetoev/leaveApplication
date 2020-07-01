package com.team2.laps.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.team2.laps.exception.AppException;
import com.team2.laps.model.Role;
import com.team2.laps.model.RoleName;
import com.team2.laps.model.User;
import com.team2.laps.payload.ApiResponse;
import com.team2.laps.payload.JwtAuthenticationResponse;
import com.team2.laps.payload.LoginRequest;
import com.team2.laps.payload.SignUpRequest;
import com.team2.laps.repository.LeaveRepository;
import com.team2.laps.repository.RoleRepository;
import com.team2.laps.repository.UserRepository;
import com.team2.laps.security.JwtTokenProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);

	@Autowired
	LeaveRepository leaveRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenProvider tokenProvider;

	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByName(auth.getName()).get();
	}

	public ApiResponse registerUser(SignUpRequest signUpRequest) {
		if (userRepository.existsByName(signUpRequest.getName())) {
			return new ApiResponse(false, "Username is already taken!");
		}
		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ApiResponse(false, "Email Address already in use!");
		}
		User user = new User(signUpRequest.getName(), signUpRequest.getEmail(), signUpRequest.getPassword());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Role userRole = roleRepository.findByName(signUpRequest.getRole())
				.orElseThrow(() -> new AppException("User Role not set."));
		user.setRoles(Collections.singleton(userRole));
		if (userRepository.save(user) != null)
			return new ApiResponse(true, "User registered successfully");
		else
			return new ApiResponse(false, "User registration failed");
	}

	public JwtAuthenticationResponse signInUser(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getNameOrEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateToken(authentication);
		RoleName roleName = userRepository.findById(tokenProvider.getUserIdFromJWT(jwt)).get().getRoles().iterator()
				.next().getName();
		return new JwtAuthenticationResponse(jwt, roleName);
	}

	public void deleteUser(String id) {
		userRepository.deleteById(id);
	}

	public ApiResponse updateUser(String id, User user) {
		// if (userRepository.findById(id).get().getEmail() == user.getEmail()
		// || userRepository.findById(id).get().getUsername() == user.getUsername()) {
		// if (userRepository.existsByUsername(user.getUsername())) {
		// return new ApiResponse(false, "Username is already taken!");
		// }
		// if (userRepository.existsByEmail(user.getEmail())) {
		// return new ApiResponse(false, "Email Address already in use!");
		// }
		// }
		user.getRoles().forEach(x -> logger.error(x.getName().toString()));
		// User oldUser = userRepository.findById(user.getReportTo().getId()).get();
		if (user.getReportTo() != null && user.getRoles().iterator().next().getName() != RoleName.ROLE_MANAGER) {
			if (userRepository.findById(user.getReportTo().getId()).isPresent()) {
				user.setReportTo(userRepository.findById(user.getReportTo().getId()).get());
			}
		} else if (user.getRoles().iterator().next().getName() == RoleName.ROLE_MANAGER) {
			user.setReportTo(null);
			user.setAnnualLeaveEntitled(0);
			user.setAnnualLeaveLeft(0);
			user.setCompensationLeft(0);
			user.setMedicalLeaveLeft(0);
		} else {
			user.setReportTo(null);
		}
		// logger.error(oldUser.getPassword());
		// user.setPassword(oldUser.getPassword());
		user.setId(id);
		if (userRepository.save(user) != null)
			return new ApiResponse(true, "User updated successfully");
		else
			return new ApiResponse(false, "User update failed");
	}

	public List<User> getAll(RoleName role) {
		if (role == RoleName.ROLE_MANAGER) {
			return userRepository.findAll().stream()
					.filter(x -> x.getRoles().iterator().next().getName() == RoleName.ROLE_MANAGER)
					.collect(Collectors.toList());
		} else {
			return userRepository.findAll().stream()
					.filter(x -> x.getRoles().iterator().next().getName() != RoleName.ROLE_ADMIN)
					.collect(Collectors.toList());
		}

	}
}