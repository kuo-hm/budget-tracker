package com.budget_tracker.tracker.budget_tracker.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Integer currentXp;
    
    @Column(nullable = false)
    private Integer level;
    
    @Column(nullable = false)
    private Integer xpToNextLevel;
    
    @Column(nullable = false)
    private Integer savingStreak;
    
    @Column(nullable = false)
    private LocalDateTime lastSavingDate;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;
    
    // Helper methods
    public void addXp(int amount) {
        this.currentXp += amount;
        if (this.currentXp >= this.xpToNextLevel) {
            levelUp();
        }
    }
    
    private void levelUp() {
        this.level += 1;
        this.currentXp = this.currentXp - this.xpToNextLevel;
        this.xpToNextLevel = calculateNextLevelXp();
    }
    
    private int calculateNextLevelXp() {
        // Formula: Base XP (100) + (Level * 50)
        return 100 + (this.level * 50);
    }
    
    public void updateSavingStreak(LocalDateTime transactionDate) {
        if (lastSavingDate == null || 
            transactionDate.toLocalDate().isAfter(lastSavingDate.toLocalDate())) {
            savingStreak++;
            lastSavingDate = transactionDate;
        }
    }
    
    public void resetSavingStreak() {
        this.savingStreak = 0;
    }
} 