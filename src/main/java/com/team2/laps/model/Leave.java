package com.team2.laps.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class Leave {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated
    private LeaveType leaveType;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDateTime endDate;

    @NotBlank
    private String reason;

    private String workDissemination;

    private String contactDetails;

    @Enumerated
    private LeaveStatus status;

    private String rejectReason;
}