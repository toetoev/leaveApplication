package com.team2.laps.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.team2.laps.model.User;
import com.team2.laps.repository.LeaveRepository;
import com.team2.laps.repository.UserRepository;

@Service
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);
	
    @Autowired
    LeaveRepository lrepo;

    @Autowired
    UserRepository urepo;

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return urepo.findByUsername(auth.getName()).get();
    }

    
	public boolean saveUser(User user) {
	if (urepo.save(user)!=null) return true; 
	else return false;
	}
	
	public void deleteUser(Long id) {
	urepo.deleteById(id);
	}
	
	public List<User> findAll() {
	return urepo.findAll();
	}
	
	public User findById(Long id) {
	return urepo.findById(id).get();
	}
	
	public User findUserByName(String name) {
	return urepo.findByName(name).get();
	}
    
}