package com.team2.laps.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team2.laps.model.Role;
import com.team2.laps.model.RoleName;
import com.team2.laps.model.User;
import com.team2.laps.payload.SignUpRequest;
import com.team2.laps.repository.RoleRepository;
import com.team2.laps.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
	@Autowired
	private UserService uservice;
	
	@GetMapping("/users")
	public List<User> listUsers(){
		return uservice.findAll();
	}
	
	@DeleteMapping("/{id}")
	public List<User> deleteUser(@PathVariable Long id) {
		uservice.deleteUser(id);
		return uservice.findAll();
	}
	
	@PostMapping("/create")
	public List<User> addUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		uservice.saveUser(user);
		return uservice.findAll();
	}
	
	@PutMapping("/{id}")
	public List<User> editUser(@PathVariable Long id, @RequestBody User updatedUser){
		User currentUser = uservice.findById(id);
		// User manager = uservice.findByName(updatedUser.getReportTo());
		User manager = updatedUser.getReportTo();
		Role role = roleRepository.findByName(RoleName.ROLE_MANAGER).get();
		currentUser.setName(updatedUser.getName());
		currentUser.setUsername(updatedUser.getUsername());
		currentUser.setEmail(updatedUser.getEmail());
		currentUser.setPassword(updatedUser.getPassword());
		currentUser.setGender(updatedUser.getGender());
		currentUser.setAnnualLeaveEntitled(updatedUser.getAnnualLeaveEntitled());
		// currentUser.setRoles(Collections.singleton(assignRole));
		uservice.saveUser(currentUser);
		return uservice.findAll();
	}

}
