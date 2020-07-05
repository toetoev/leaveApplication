package com.team2.laps.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team2.laps.validation.ClaimDate;
import com.team2.laps.validation.LeaveIDExisting;
import com.team2.laps.validation.OnCreate;
import com.team2.laps.validation.OnUpdate;
import com.team2.laps.validation.RejectReason;
import com.team2.laps.validation.StatusChange;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leaves")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ClaimDate(groups = { OnUpdate.class, OnCreate.class })
@RejectReason(groups = OnUpdate.class)
@StatusChange(groups = { OnUpdate.class, OnCreate.class })
public class Leave {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Null(groups = OnCreate.class)
    @LeaveIDExisting(groups = OnUpdate.class)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated
    private LeaveType leaveType;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate startDate;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate endDate;

    @NotBlank(message = "{NotBlank.reason}")
    private String reason;

    private String workDissemination;

    private String contactDetails;

    @Enumerated
    private LeaveStatus status;

    private String rejectReason;
}