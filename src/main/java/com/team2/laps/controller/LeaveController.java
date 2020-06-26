package com.team2.laps.controller;

import java.util.List;

import javax.validation.Valid;

import com.team2.laps.model.Leave;
import com.team2.laps.service.LeaveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leave")
public class LeaveController {
    @Autowired
    LeaveService leaveService;

    @GetMapping
    public List<Leave> getLeaveByUser() {
        return leaveService.getLeaveByUser();
    }

    @PostMapping
    public ResponseEntity<?> createOrUpdateLeave(@Valid @RequestBody Leave leave) {
        return ResponseEntity.ok(leaveService.createOrUpdateLeave(leave));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable String id) {
        leaveService.deleteLeave(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}