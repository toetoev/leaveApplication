package com.team2.laps.controller;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import com.team2.laps.model.Leave;
import com.team2.laps.model.TimePeriod;
import com.team2.laps.payload.ApiResponse;
import com.team2.laps.service.LeaveService;
import com.team2.laps.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {
    private static final Logger logger = LoggerFactory.getLogger(LeaveService.class);

    @Autowired
    LeaveService leaveService;

    @Autowired
    UserService userService;

    @GetMapping
    @RolesAllowed({ "ROLE_ADMINISTRATIVE_STAFF", "ROLE_PROFESSIONAL_STAFF", "ROLE_MANAGER" })
    public ResponseEntity<?> getLeaveByUser(@RequestParam(required = false) TimePeriod timePeriod,
            @RequestParam(required = false) String leaveId) {
        return ResponseEntity.ok(new ApiResponse(leaveService.getLeaveByUser(timePeriod, leaveId)));
    }

    // TODO: test validation
    @PostMapping
    @RolesAllowed({ "ROLE_ADMINISTRATIVE_STAFF", "ROLE_PROFESSIONAL_STAFF", "ROLE_MANAGER" })
    public ResponseEntity<?> createOrUpdateLeave(@Valid @RequestBody Leave leave, Authentication authentication,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.ok(new ApiResponse(false, "Invalid Leave"));
        }
        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));
        leave.setUser(userService.getCurrentUser());
        return ResponseEntity
                .ok(new ApiResponse(leaveService.createOrUpdateLeave(leave, isManager), "Wrong Data Content Entered"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable String id) {
        leaveService.deleteLeave(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}