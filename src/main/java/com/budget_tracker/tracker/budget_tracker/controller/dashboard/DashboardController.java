package com.budget_tracker.tracker.budget_tracker.controller.dashboard;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.CategoryStatsDTO;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.DashboardSummaryResponse;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.DateRangeDTO;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.SpendingTrendsResponse;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.UserStatsDTO;
import com.budget_tracker.tracker.budget_tracker.services.dashboard.DashboardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get the user's dashboard summary
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getUserDashboard(
            HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        DashboardSummaryResponse response = dashboardService.getUserDashboardSummary(
                userEmail, null);  // Use default date range
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get the user's dashboard summary with custom date range
     */
    @PostMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getUserDashboardWithDateRange(
            @RequestBody DateRangeDTO dateRange,
            HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        DashboardSummaryResponse response = dashboardService.getUserDashboardSummary(
                userEmail, dateRange);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Admin endpoint to get system-wide dashboard summary
     */
    @GetMapping("/admin/summary")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DashboardSummaryResponse> getAdminDashboard() {
        DashboardSummaryResponse response = dashboardService.getAdminDashboardSummary(null);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Admin endpoint to get system-wide dashboard summary with custom date range
     */
    @PostMapping("/admin/summary")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<DashboardSummaryResponse> getAdminDashboardWithDateRange(
            @RequestBody DateRangeDTO dateRange) {
        DashboardSummaryResponse response = dashboardService.getAdminDashboardSummary(dateRange);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Admin endpoint to get user statistics
     */
    @GetMapping("/admin/user-stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserStatsDTO>> getUserStats() {
        List<UserStatsDTO> response = dashboardService.getAllUserStats(null);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Admin endpoint to get user statistics with custom date range
     */
    @PostMapping("/admin/user-stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserStatsDTO>> getUserStatsWithDateRange(
            @RequestBody DateRangeDTO dateRange) {
        List<UserStatsDTO> response = dashboardService.getAllUserStats(dateRange);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Admin endpoint to get category statistics
     */
    @GetMapping("/admin/category-stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CategoryStatsDTO>> getCategoryStats() {
        List<CategoryStatsDTO> response = dashboardService.getCategoryStats(null);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Admin endpoint to get category statistics with custom date range
     */
    @PostMapping("/admin/category-stats")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<CategoryStatsDTO>> getCategoryStatsWithDateRange(
            @RequestBody DateRangeDTO dateRange) {
        List<CategoryStatsDTO> response = dashboardService.getCategoryStats(dateRange);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get spending trends and insights for the user
     */
    @GetMapping("/spending-trends")
    public ResponseEntity<SpendingTrendsResponse> getSpendingTrends(HttpServletRequest httpRequest) {
        String userEmail = (String) httpRequest.getAttribute("userEmail");
        SpendingTrendsResponse response = dashboardService.getSpendingTrends(userEmail);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Admin endpoint to get spending trends and insights for a specific user
     */
    @GetMapping("/admin/user/{userEmail}/spending-trends")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<SpendingTrendsResponse> getUserSpendingTrends(@PathVariable String userEmail) {
        SpendingTrendsResponse response = dashboardService.getSpendingTrends(userEmail);
        return ResponseEntity.ok(response);
    }
} 