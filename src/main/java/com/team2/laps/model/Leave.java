package com.team2.laps.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.format.annotation.DateTimeFormat;

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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    @Enumerated
    private LeaveType leaveType;

    @DateTimeFormat(pattern = "yyyy-MM-dd.HH")
    private Date startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd.HH")
    private Date endDate;

    private String reason;

    private String workDissemination;

    private String contactDetails;

    @Enumerated
    private LeaveStatus status;

    private String rejectReason;
}