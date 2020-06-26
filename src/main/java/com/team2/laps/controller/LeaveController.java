package com.team2.laps.controller;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import com.team2.laps.model.Leave;
import com.team2.laps.model.TimePeriod;
import com.team2.laps.service.LeaveService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping
    @RolesAllowed({ "ROLE_ADMINISTRATIVE_STAFF", "ROLE_PROFESSIONAL_STAFF", "ROLE_MANAGER" })
    public List<Leave> getLeaveByUser(@RequestParam(required = false) TimePeriod timePeriod,
            @RequestParam(required = false) String leaveId) {
        return leaveService.getLeaveByUser(timePeriod, leaveId);
    }

    // FIXME: validate start date and end date
    @PostMapping
    @RolesAllowed({ "ROLE_ADMINISTRATIVE_STAFF", "ROLE_PROFESSIONAL_STAFF", "ROLE_MANAGER" })
    public ResponseEntity<?> createOrUpdateLeave(@Valid @RequestBody Leave leave) {
        return ResponseEntity.ok(leaveService.createOrUpdateLeave(leave));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable String id) {
        leaveService.deleteLeave(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}