package com.budget_tracker.tracker.budget_tracker.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.budget_tracker.tracker.budget_tracker.enums.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "email"),}
)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "createdBy")
    @JsonManagedReference
    private List<Categories> categories; // List of categories created by the user

    @OneToMany(mappedBy = "createdBy")
    @JsonManagedReference
    private List<Transaction> transaction; // List of categories created by the user

    @OneToMany(mappedBy = "createdBy")
    @JsonManagedReference
    private List<Budget> budget; // List of categories created by the user

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return (firstName + " " + lastName);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @PrePersist
    @PreUpdate
    public void encodePassword() {
        if (this.password != null && !this.password.isEmpty()) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            this.password = encoder.encode(this.password);
        }
    }

    public void setPassword(String password, BCryptPasswordEncoder passwordEncoder) {
        if (password != null && !password.isEmpty()) {
            this.password = passwordEncoder.encode(password);
        }
    }

}
