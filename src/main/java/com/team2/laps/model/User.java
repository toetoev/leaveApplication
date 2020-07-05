package com.team2.laps.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreRemove;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.team2.laps.validation.AnnualLeave;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "name") })
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "leaves" })
@AnnualLeave(message = "Annual leave entitled cannot be lower than leave used the year")
public class User {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    @Size(max = 40)
    @NotBlank(message = "Name should not be blank.")
    private String name;

    @Size(max = 40)
    @NotBlank(message = "Email should not be blank.")
    @Email(message = "Email format is wrong.")
    private String email;

    @Size(max = 100, message = "Password max length should be lower than 100")
    @JsonIgnore
    private String password;

    private long annualLeaveEntitled;
    private long annualLeaveLeft;

    @Max(60)
    private long medicalLeaveLeft;

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<Role>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_to")
    private User reportTo;

    @OneToMany(mappedBy = "reportTo", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<User> subordinates = new HashSet<User>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Leave> leaves = new HashSet<Leave>();

    @PreRemove
    private void removeUser() {
        roles.clear();
        for (User user : subordinates) {
            user.setReportTo(null);
        }
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String id) {
        this.id = id;
    }

    public User(Set<Role> role) {
        this.roles = role;
    }

    public boolean isRole(RoleName roleName) {
        if (!this.getRoles().isEmpty())
            return this.getRoles().iterator().next().getName() == roleName;
        else
            return false;
    }
}