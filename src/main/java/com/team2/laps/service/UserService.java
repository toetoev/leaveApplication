package com.team2.laps.service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import com.team2.laps.exception.AppException;
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
import com.team2.laps.validation.UserIDExisting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
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
	@Validated
	public ApiResponse registerUser(@Valid SignUpRequest signUpRequest) {
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
			return ApiResponse.builder().success(true).message("User registered successfully").build();
		else
			return ApiResponse.builder().success(false).message("User registration failed").build();
	}

	@Transactional
	public JwtAuthenticationResponse signInUser(@Valid LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getNameOrEmail(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateToken(authentication);
		String roleName = authentication.getAuthorities().stream().iterator().next().getAuthority();
		String name = authentication.getName();
		return new JwtAuthenticationResponse(jwt, roleName, name);
	}

	@Transactional
	public void deleteUser(@Valid @UserIDExisting String id) {
		userRepository.deleteById(id);
	}

	@Transactional
	@Validated
	public ApiResponse updateUser(@UserIDExisting String id, @Valid User user) {
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
			if (!oldUser.isRole(user.getRoles().iterator().next().getName())) {
				if (user.isRole(RoleName.ROLE_MANAGER)) {
					user.setReportTo(null);
					user.setAnnualLeaveEntitled(0);
					user.setAnnualLeaveLeft(0);
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
					if (oldUser.isRole(RoleName.ROLE_MANAGER)) {
						Set<User> subordinates = oldUser.getSubordinates();
						subordinates.forEach(x -> x.setReportTo(null));
						user.setSubordinates(subordinates);
					}
				}
			}
			// Give option for admin to set leave left different from default setting
			if (oldUser.getAnnualLeaveEntitled() != user.getAnnualLeaveEntitled()) {
				user.setAnnualLeaveLeft(user.getAnnualLeaveEntitled());
			}
			user.setAnnualLeaveLeft(user.getAnnualLeaveEntitled()
					- leaveRepository.countCurrentYearLeaveUsed(id, LeaveType.ANNUAL.ordinal()));
			user.setMedicalLeaveLeft(
					medicalLeaveMax - leaveRepository.countCurrentYearLeaveUsed(id, LeaveType.MEDICAL.ordinal()));
			// Set id and password untouched
			user.setId(id);
			user.setPassword(oldUser.getPassword());
			if (userRepository.save(user) != null)
				return ApiResponse.builder().success(true).message("User updated successfully").build();
			else
				return ApiResponse.builder().success(false).message("User update failed").build();
		} else
			return ApiResponse.builder().success(false).message("Cannot find user to be updated").build();
	}

	@Transactional
	public ApiResponse getAll(RoleName role) {
		// Get all manager
		if (role == RoleName.ROLE_MANAGER) {
			return ApiResponse.builder()
					.data(userRepository.findAll().stream()
							.filter(x -> x.getRoles().iterator().next().getName() == RoleName.ROLE_MANAGER)
							.collect(Collectors.toList()))
					.build();
		}
		// Default action get all except admin
		else {
			return ApiResponse.builder()
					.data(userRepository.findAll().stream()
							.filter(x -> x.getRoles().iterator().next().getName() != RoleName.ROLE_ADMIN)
							.collect(Collectors.toList()))
					.build();
		}
	}
}