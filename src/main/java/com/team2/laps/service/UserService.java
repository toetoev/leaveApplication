package com.team2.laps.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.team2.laps.exception.AppException;
import com.team2.laps.model.LeaveStatus;
import com.team2.laps.model.LeaveType;
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
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

	@Value("${app.administrativeStaff.annualLeaveEntitled}")
	private int administrativeStaffAnnualLeaveEntitled;

	@Value("${app.professionalStaff.annualLeaveEntitled}")
	private int professionalStaffAnnualLeaveEntitled;

	@Value("${app.medicalLeaveMax}")
	private int medicalLeaveMax;

	@Transactional
	public User getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.findByName(auth.getName()).get();
	}

	@Transactional
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
		if (userRole.getName() == RoleName.ROLE_ADMINISTRATIVE_STAFF) {
			user.setAnnualLeaveEntitled(administrativeStaffAnnualLeaveEntitled);
			user.setAnnualLeaveLeft(administrativeStaffAnnualLeaveEntitled);
			user.setMedicalLeaveLeft(medicalLeaveMax);
		} else if (userRole.getName() == RoleName.ROLE_PROFESSIONAL_STAFF) {
			user.setAnnualLeaveEntitled(professionalStaffAnnualLeaveEntitled);
			user.setAnnualLeaveLeft(professionalStaffAnnualLeaveEntitled);
			user.setMedicalLeaveLeft(medicalLeaveMax);
		}
		if (userRepository.save(user) != null)
			return new ApiResponse(true, "User registered successfully");
		else
			return new ApiResponse(false, "User registration failed");
	}

	@Transactional
	public JwtAuthenticationResponse signInUser(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getNameOrEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateToken(authentication);
		String roleName = authentication.getAuthorities().stream().iterator().next().getAuthority();
		return new JwtAuthenticationResponse(jwt, roleName);
	}

	@Transactional
	public void deleteUser(String id) {
		if (id != null || userRepository.findById(id).isPresent())
			userRepository.deleteById(id);
	}

	@Transactional
	public ApiResponse updateUser(String id, User user) {
		if (id != null || userRepository.findById(id).isPresent()) {
			User oldUser = userRepository.findById(id).get();
			// Report To
			if (user.getReportTo() != null && !user.isRole(RoleName.ROLE_MANAGER)) {
				if (userRepository.findById(user.getReportTo().getId()).isPresent()) {
					user.setReportTo(userRepository.findById(user.getReportTo().getId()).get());
				}
			}
			// Handle role change
			// -> Manager, initialize with 0
			if (user.isRole(RoleName.ROLE_MANAGER)) {
				user.setReportTo(null);
				user.setAnnualLeaveEntitled(0);
				user.setAnnualLeaveLeft(0);
				user.setCompensationLeft(0);
				user.setMedicalLeaveLeft(0);
			}
			// -> Staff, initialize with default leave
			else {
				if (user.isRole(RoleName.ROLE_ADMINISTRATIVE_STAFF)) {
					user.setAnnualLeaveEntitled(administrativeStaffAnnualLeaveEntitled);
					user.setAnnualLeaveLeft(administrativeStaffAnnualLeaveEntitled);
					user.setMedicalLeaveLeft(medicalLeaveMax);
				} else if (user.isRole(RoleName.ROLE_PROFESSIONAL_STAFF)) {
					user.setAnnualLeaveEntitled(professionalStaffAnnualLeaveEntitled);
					user.setAnnualLeaveLeft(professionalStaffAnnualLeaveEntitled);
					user.setMedicalLeaveLeft(medicalLeaveMax);
				}
			}
			// Calculate correct leave left
			user.setAnnualLeaveLeft(user.getAnnualLeaveEntitled()
					- leaveRepository.countCurrentYearLeaveUsed(id, LeaveType.ANNUAL, LeaveStatus.APPROVED));
			user.setMedicalLeaveLeft(medicalLeaveMax
					- leaveRepository.countCurrentYearLeaveUsed(id, LeaveType.MEDICAL, LeaveStatus.APPROVED));
			// Give option for admin to set leave left different from default setting
			if (oldUser.getAnnualLeaveEntitled() != user.getAnnualLeaveEntitled()) {
				user.setAnnualLeaveLeft(user.getAnnualLeaveEntitled());
			}
			// Set id and password untouched
			user.setId(id);
			user.setPassword(oldUser.getPassword());
			if (userRepository.save(user) != null)
				return new ApiResponse(true, "User updated successfully");
			else
				return new ApiResponse(false, "User update failed");
		} else
			return new ApiResponse(false, "Cannot find user to be updated");
	}

	@Transactional
	public List<User> getAll(RoleName role) {
		// Get all manager
		if (role == RoleName.ROLE_MANAGER) {
			return userRepository.findAll().stream()
					.filter(x -> x.getRoles().iterator().next().getName() == RoleName.ROLE_MANAGER)
					.collect(Collectors.toList());
		}
		// Default action get all except admin
		else {
			return userRepository.findAll().stream()
					.filter(x -> x.getRoles().iterator().next().getName() != RoleName.ROLE_ADMIN)
					.collect(Collectors.toList());
		}
	}
}