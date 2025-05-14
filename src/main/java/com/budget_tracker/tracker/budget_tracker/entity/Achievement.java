package com.budget_tracker.tracker.budget_tracker.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.budget_tracker.tracker.budget_tracker.enums.AchievementType;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "achievements")
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    private String description;
    
    private String icon;
    
    @Column(nullable = false)
    private Integer pointsAwarded;
    
    @CreationTimestamp
    private LocalDateTime earnedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType achievementType;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
} 