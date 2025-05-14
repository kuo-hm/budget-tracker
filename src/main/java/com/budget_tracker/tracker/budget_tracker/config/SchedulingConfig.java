package com.budget_tracker.tracker.budget_tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.budget_tracker.tracker.budget_tracker.services.transaction.RecurringTransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final RecurringTransactionService recurringTransactionService;
    
    /**
     * Scheduled task that runs at midnight every day to process recurring transactions
     * that are due for the current day.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    public void processRecurringTransactions() {
        log.info("Starting scheduled job to process recurring transactions");
        try {
            recurringTransactionService.processRecurringTransactions();
            log.info("Recurring transactions processed successfully");
        } catch (Exception e) {
            log.error("Error processing recurring transactions: {}", e.getMessage(), e);
        }
    }
} 