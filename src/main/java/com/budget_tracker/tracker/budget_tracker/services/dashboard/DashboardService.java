package com.budget_tracker.tracker.budget_tracker.services.dashboard;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.CategoryStatsDTO;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.DashboardSummaryResponse;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.DateRangeDTO;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.GoalSummaryDTO;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.SpendingInsightDTO;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.SpendingTrendsResponse;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.TransactionSummaryDTO;
import com.budget_tracker.tracker.budget_tracker.controller.dashboard.dto.UserStatsDTO;
import com.budget_tracker.tracker.budget_tracker.entity.Budget;
import com.budget_tracker.tracker.budget_tracker.entity.Categories;
import com.budget_tracker.tracker.budget_tracker.entity.Goal;
import com.budget_tracker.tracker.budget_tracker.entity.RecurringTransaction;
import com.budget_tracker.tracker.budget_tracker.entity.Transaction;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.enums.CategoryType;
import com.budget_tracker.tracker.budget_tracker.enums.GoalStatus;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.BudgetRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.CategoriesRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.GoalRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.RecurringTransactionRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.TransactionRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final CategoriesRepository categoriesRepository;
    private final UserRepository userRepository;

    /**
     * Get a summary of the dashboard for the current user.
     * 
     * @param userEmail The email of the current user
     * @param dateRange Optional date range for filtering
     * @return A summary of the user's financial data
     */
    public DashboardSummaryResponse getUserDashboardSummary(String userEmail, DateRangeDTO dateRange) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));

        LocalDateTime startDate = dateRange != null && dateRange.getStartDate() != null 
                ? dateRange.getStartDate() 
                : LocalDateTime.now().minusMonths(3);  // Default to last 3 months
                
        LocalDateTime endDate = dateRange != null && dateRange.getEndDate() != null 
                ? dateRange.getEndDate() 
                : LocalDateTime.now();

        // Get all user transactions within date range
        List<Transaction> transactions = transactionRepository.findAllByCreatedByAndTransactionDateBetween(
                user, startDate, endDate);

        // Calculate income and expenses
        Double totalIncome = calculateTotalByType(transactions, CategoryType.INCOME);
        Double totalExpenses = calculateTotalByType(transactions, CategoryType.EXPENSE);
        Double netBalance = totalIncome - totalExpenses;
        Double savingsRate = totalIncome > 0 ? (totalIncome - totalExpenses) / totalIncome * 100 : 0.0;

        // Budget analysis
        List<Budget> budgets = budgetRepository.findAllByCreatedBy(user);
        Integer totalBudgets = budgets.size();
        Integer budgetsOverBudget = 0;
        Double totalBudgetAmount = 0.0;
        Double totalBudgetSpent = 0.0;

        Map<Long, Double> categorySpending = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType() == CategoryType.EXPENSE && transaction.getTransactionCategory() != null) {
                Long categoryId = transaction.getTransactionCategory().getId();
                categorySpending.put(categoryId, 
                        categorySpending.getOrDefault(categoryId, 0.0) + transaction.getAmount());
            }
        }

        for (Budget budget : budgets) {
            Double spent = categorySpending.getOrDefault(
                    budget.getCategory() != null ? budget.getCategory().getId() : -1L, 0.0);
            totalBudgetAmount += budget.getAmount();
            totalBudgetSpent += spent;
            
            if (spent > budget.getAmount()) {
                budgetsOverBudget++;
            }
        }

        Integer budgetsOnTrack = totalBudgets - budgetsOverBudget;
        Double budgetUtilizationPercentage = totalBudgetAmount > 0 
                ? (totalBudgetSpent / totalBudgetAmount) * 100 
                : 0.0;

        // Goals analysis
        List<Goal> goals = goalRepository.findAllByCreatedBy(user);
        Integer totalGoals = goals.size();
        Integer completedGoals = (int) goals.stream()
                .filter(g -> g.getStatus() == GoalStatus.COMPLETED)
                .count();
        Integer inProgressGoals = (int) goals.stream()
                .filter(g -> g.getStatus() == GoalStatus.IN_PROGRESS)
                .count();
        
        Double totalProgress = goals.stream()
                .mapToDouble(g -> {
                    if (g.getTargetAmount() > 0) {
                        return (g.getCurrentAmount() / g.getTargetAmount()) * 100;
                    }
                    return 0.0;
                })
                .sum();
        
        Double averageGoalCompletion = totalGoals > 0 ? totalProgress / totalGoals : 0.0;

        // Recurring transactions
        List<RecurringTransaction> recurringTransactions = 
                recurringTransactionRepository.findAllByCreatedBy(user);
        Integer totalRecurringTransactions = recurringTransactions.size();
        
        Double monthlyRecurringIncome = recurringTransactions.stream()
                .filter(rt -> rt.getType() == CategoryType.INCOME && rt.isActive())
                .mapToDouble(RecurringTransaction::getAmount)
                .sum();
        
        Double monthlyRecurringExpenses = recurringTransactions.stream()
                .filter(rt -> rt.getType() == CategoryType.EXPENSE && rt.isActive())
                .mapToDouble(RecurringTransaction::getAmount)
                .sum();

        // Category statistics
        Map<String, Double> topExpenseCategories = getCategoryStatistics(transactions, CategoryType.EXPENSE, 5);
        Map<String, Double> topIncomeCategories = getCategoryStatistics(transactions, CategoryType.INCOME, 5);

        // Time-based analysis
        Map<String, Double> expensesByMonth = getMonthlyBreakdown(transactions, CategoryType.EXPENSE);
        Map<String, Double> incomeByMonth = getMonthlyBreakdown(transactions, CategoryType.INCOME);

        // Recent activity
        List<TransactionSummaryDTO> recentTransactions = getRecentTransactions(user, 5);
        List<GoalSummaryDTO> recentGoals = getRecentGoals(user, 5);

        // Build response
        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .savingsRate(savingsRate)
                .totalBudgets(totalBudgets)
                .budgetsOnTrack(budgetsOnTrack)
                .budgetsOverBudget(budgetsOverBudget)
                .budgetUtilizationPercentage(budgetUtilizationPercentage)
                .totalGoals(totalGoals)
                .completedGoals(completedGoals)
                .inProgressGoals(inProgressGoals)
                .averageGoalCompletion(averageGoalCompletion)
                .totalRecurringTransactions(totalRecurringTransactions)
                .monthlyRecurringIncome(monthlyRecurringIncome)
                .monthlyRecurringExpenses(monthlyRecurringExpenses)
                .topExpenseCategories(topExpenseCategories)
                .topIncomeCategories(topIncomeCategories)
                .expensesByMonth(expensesByMonth)
                .incomeByMonth(incomeByMonth)
                .recentTransactions(recentTransactions)
                .recentGoals(recentGoals)
                .build();
    }

    /**
     * Get detailed statistics for admin dashboard
     * 
     * @param dateRange Optional date range for filtering
     * @return A summary of all system financial data
     */
    public DashboardSummaryResponse getAdminDashboardSummary(DateRangeDTO dateRange) {
        LocalDateTime startDate = dateRange != null && dateRange.getStartDate() != null 
                ? dateRange.getStartDate() 
                : LocalDateTime.now().minusMonths(3);  // Default to last 3 months
                
        LocalDateTime endDate = dateRange != null && dateRange.getEndDate() != null 
                ? dateRange.getEndDate() 
                : LocalDateTime.now();

        // Get all transactions within date range
        List<Transaction> transactions = transactionRepository.findAllByTransactionDateBetween(
                startDate, endDate);

        // Calculate income and expenses
        Double totalIncome = calculateTotalByType(transactions, CategoryType.INCOME);
        Double totalExpenses = calculateTotalByType(transactions, CategoryType.EXPENSE);
        Double netBalance = totalIncome - totalExpenses;
        Double savingsRate = totalIncome > 0 ? (totalIncome - totalExpenses) / totalIncome * 100 : 0.0;

        // Budget analysis
        List<Budget> budgets = budgetRepository.findAll();
        Integer totalBudgets = budgets.size();
        Integer budgetsOverBudget = 0;
        Double totalBudgetAmount = 0.0;
        Double totalBudgetSpent = 0.0;

        Map<Long, Double> categorySpending = new HashMap<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType() == CategoryType.EXPENSE && transaction.getTransactionCategory() != null) {
                Long categoryId = transaction.getTransactionCategory().getId();
                categorySpending.put(categoryId, 
                        categorySpending.getOrDefault(categoryId, 0.0) + transaction.getAmount());
            }
        }

        for (Budget budget : budgets) {
            Double spent = categorySpending.getOrDefault(
                    budget.getCategory() != null ? budget.getCategory().getId() : -1L, 0.0);
            totalBudgetAmount += budget.getAmount();
            totalBudgetSpent += spent;
            
            if (spent > budget.getAmount()) {
                budgetsOverBudget++;
            }
        }

        Integer budgetsOnTrack = totalBudgets - budgetsOverBudget;
        Double budgetUtilizationPercentage = totalBudgetAmount > 0 
                ? (totalBudgetSpent / totalBudgetAmount) * 100 
                : 0.0;

        // Goals analysis
        List<Goal> goals = goalRepository.findAll();
        Integer totalGoals = goals.size();
        Integer completedGoals = (int) goals.stream()
                .filter(g -> g.getStatus() == GoalStatus.COMPLETED)
                .count();
        Integer inProgressGoals = (int) goals.stream()
                .filter(g -> g.getStatus() == GoalStatus.IN_PROGRESS)
                .count();
        
        Double totalProgress = goals.stream()
                .mapToDouble(g -> {
                    if (g.getTargetAmount() > 0) {
                        return (g.getCurrentAmount() / g.getTargetAmount()) * 100;
                    }
                    return 0.0;
                })
                .sum();
        
        Double averageGoalCompletion = totalGoals > 0 ? totalProgress / totalGoals : 0.0;

        // Recurring transactions
        List<RecurringTransaction> recurringTransactions = 
                recurringTransactionRepository.findAll();
        Integer totalRecurringTransactions = recurringTransactions.size();
        
        Double monthlyRecurringIncome = recurringTransactions.stream()
                .filter(rt -> rt.getType() == CategoryType.INCOME && rt.isActive())
                .mapToDouble(RecurringTransaction::getAmount)
                .sum();
        
        Double monthlyRecurringExpenses = recurringTransactions.stream()
                .filter(rt -> rt.getType() == CategoryType.EXPENSE && rt.isActive())
                .mapToDouble(RecurringTransaction::getAmount)
                .sum();

        // Category statistics
        Map<String, Double> topExpenseCategories = getCategoryStatistics(transactions, CategoryType.EXPENSE, 10);
        Map<String, Double> topIncomeCategories = getCategoryStatistics(transactions, CategoryType.INCOME, 10);

        // Time-based analysis
        Map<String, Double> expensesByMonth = getMonthlyBreakdown(transactions, CategoryType.EXPENSE);
        Map<String, Double> incomeByMonth = getMonthlyBreakdown(transactions, CategoryType.INCOME);

        // Recent activity - for admin, get system-wide recent activity
        List<TransactionSummaryDTO> recentTransactions = transactions.stream()
                .sorted(Comparator.comparing(Transaction::getCreatedAt).reversed())
                .limit(10)
                .map(this::mapToTransactionSummaryDTO)
                .collect(Collectors.toList());
                
        List<GoalSummaryDTO> recentGoals = goals.stream()
                .sorted(Comparator.comparing(Goal::getCreatedAt).reversed())
                .limit(10)
                .map(this::mapToGoalSummaryDTO)
                .collect(Collectors.toList());

        // Build response
        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .savingsRate(savingsRate)
                .totalBudgets(totalBudgets)
                .budgetsOnTrack(budgetsOnTrack)
                .budgetsOverBudget(budgetsOverBudget)
                .budgetUtilizationPercentage(budgetUtilizationPercentage)
                .totalGoals(totalGoals)
                .completedGoals(completedGoals)
                .inProgressGoals(inProgressGoals)
                .averageGoalCompletion(averageGoalCompletion)
                .totalRecurringTransactions(totalRecurringTransactions)
                .monthlyRecurringIncome(monthlyRecurringIncome)
                .monthlyRecurringExpenses(monthlyRecurringExpenses)
                .topExpenseCategories(topExpenseCategories)
                .topIncomeCategories(topIncomeCategories)
                .expensesByMonth(expensesByMonth)
                .incomeByMonth(incomeByMonth)
                .recentTransactions(recentTransactions)
                .recentGoals(recentGoals)
                .build();
    }
    
    /**
     * Get detailed statistics for each user in the system (admin only)
     */
    public List<UserStatsDTO> getAllUserStats(DateRangeDTO dateRange) {
        LocalDateTime startDate = dateRange != null && dateRange.getStartDate() != null 
                ? dateRange.getStartDate() 
                : LocalDateTime.now().minusMonths(3);
                
        LocalDateTime endDate = dateRange != null && dateRange.getEndDate() != null 
                ? dateRange.getEndDate() 
                : LocalDateTime.now();
                
        List<User> users = userRepository.findAll();
        List<UserStatsDTO> userStats = new ArrayList<>();
        
        for (User user : users) {
            // Count user's transactions
            List<Transaction> userTransactions = transactionRepository.findAllByCreatedByAndTransactionDateBetween(
                    user, startDate, endDate);
            Integer transactionCount = userTransactions.size();
            
            // Calculate income and expenses
            Double totalIncome = calculateTotalByType(userTransactions, CategoryType.INCOME);
            Double totalExpenses = calculateTotalByType(userTransactions, CategoryType.EXPENSE);
            Double savingsRate = totalIncome > 0 ? (totalIncome - totalExpenses) / totalIncome * 100 : 0.0;
            
            // Get other counts
            Integer goalCount = goalRepository.countByCreatedBy(user);
            Integer budgetCount = budgetRepository.countByCreatedBy(user);
            
            // Find last activity date
            LocalDateTime lastActive = userTransactions.stream()
                    .map(Transaction::getCreatedAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(user.getCreatedAt());
            
            UserStatsDTO userStat = UserStatsDTO.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .createdAt(user.getCreatedAt())
                    .transactionCount(transactionCount)
                    .goalCount(goalCount)
                    .budgetCount(budgetCount)
                    .totalSpent(totalExpenses)
                    .totalIncome(totalIncome)
                    .savingsRate(savingsRate)
                    .lastActive(lastActive)
                    .build();
                    
            userStats.add(userStat);
        }
        
        return userStats;
    }
    
    /**
     * Get detailed statistics for each category (admin only)
     */
    public List<CategoryStatsDTO> getCategoryStats(DateRangeDTO dateRange) {
        LocalDateTime startDate = dateRange != null && dateRange.getStartDate() != null 
                ? dateRange.getStartDate() 
                : LocalDateTime.now().minusMonths(3);
                
        LocalDateTime endDate = dateRange != null && dateRange.getEndDate() != null 
                ? dateRange.getEndDate() 
                : LocalDateTime.now();
                
        List<Categories> categories = categoriesRepository.findAll();
        List<Transaction> allTransactions = transactionRepository.findAllByTransactionDateBetween(
                startDate, endDate);
                
        Double totalExpenses = calculateTotalByType(allTransactions, CategoryType.EXPENSE);
        Double totalIncome = calculateTotalByType(allTransactions, CategoryType.INCOME);
        
        List<CategoryStatsDTO> categoryStats = new ArrayList<>();
        
        for (Categories category : categories) {
            // Find transactions for this category
            List<Transaction> categoryTransactions = allTransactions.stream()
                    .filter(t -> t.getTransactionCategory() != null 
                            && t.getTransactionCategory().getId().equals(category.getId()))
                    .collect(Collectors.toList());
                            
            Integer transactionCount = categoryTransactions.size();
            Double totalAmount = categoryTransactions.stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
                    
            // Calculate percentage of total for its type
            Double percentageOfTotal = 0.0;
            if (category.getType() == CategoryType.EXPENSE && totalExpenses > 0) {
                percentageOfTotal = (totalAmount / totalExpenses) * 100;
            } else if (category.getType() == CategoryType.INCOME && totalIncome > 0) {
                percentageOfTotal = (totalAmount / totalIncome) * 100;
            }
            
            // Count unique users who have used this category
            long userCount = categoryTransactions.stream()
                    .map(t -> t.getCreatedBy().getId())
                    .distinct()
                    .count();
                    
            CategoryStatsDTO categoryStat = CategoryStatsDTO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .type(category.getType())
                    .transactionCount(transactionCount)
                    .totalAmount(totalAmount)
                    .percentageOfTotal(percentageOfTotal)
                    .userCount((int) userCount)
                    .build();
                    
            categoryStats.add(categoryStat);
        }
        
        return categoryStats;
    }

    /**
     * Get spending trends and insights for the user
     * 
     * @param userEmail The email of the user
     * @return Spending trends and personalized insights
     */
    public SpendingTrendsResponse getSpendingTrends(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
                
        // Define time periods for analysis
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(now);
        YearMonth previousMonth = currentMonth.minusMonths(1);
        YearMonth twoMonthsAgo = currentMonth.minusMonths(2);
        
        LocalDateTime currentMonthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime currentMonthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        LocalDateTime previousMonthStart = previousMonth.atDay(1).atStartOfDay();
        LocalDateTime previousMonthEnd = previousMonth.atEndOfMonth().atTime(23, 59, 59);
        LocalDateTime twoMonthsAgoStart = twoMonthsAgo.atDay(1).atStartOfDay();
        LocalDateTime twoMonthsAgoEnd = twoMonthsAgo.atEndOfMonth().atTime(23, 59, 59);
        
        // Get last 6 months for trends
        List<YearMonth> last6Months = IntStream.range(0, 6)
                .mapToObj(i -> currentMonth.minusMonths(i))
                .collect(Collectors.toList());
                
        // Get transactions for each time period
        List<Transaction> currentMonthTransactions = transactionRepository
                .findAllByCreatedByAndTransactionDateBetween(user, currentMonthStart, currentMonthEnd);
        List<Transaction> previousMonthTransactions = transactionRepository
                .findAllByCreatedByAndTransactionDateBetween(user, previousMonthStart, previousMonthEnd);
        List<Transaction> twoMonthsAgoTransactions = transactionRepository
                .findAllByCreatedByAndTransactionDateBetween(user, twoMonthsAgoStart, twoMonthsAgoEnd);
        
        // Get transactions for the last 6 months (for longer-term trends)
        LocalDateTime sixMonthsAgoStart = last6Months.get(last6Months.size() - 1).atDay(1).atStartOfDay();
        List<Transaction> last6MonthsTransactions = transactionRepository
                .findAllByCreatedByAndTransactionDateBetween(user, sixMonthsAgoStart, currentMonthEnd);
        
        // Calculate spending and income by month and category
        Map<YearMonth, Map<String, Double>> spendingByCategoryAndMonth = new LinkedHashMap<>();
        Map<YearMonth, Double> totalSpendingByMonth = new LinkedHashMap<>();
        Map<YearMonth, Double> totalIncomeByMonth = new LinkedHashMap<>();
        
        // Initialize maps for all 6 months
        for (YearMonth month : last6Months) {
            spendingByCategoryAndMonth.put(month, new HashMap<>());
            totalSpendingByMonth.put(month, 0.0);
            totalIncomeByMonth.put(month, 0.0);
        }
        
        // Populate the maps with transaction data
        for (Transaction transaction : last6MonthsTransactions) {
            if (transaction.getTransactionDate() == null) continue;
            
            YearMonth transactionMonth = YearMonth.from(transaction.getTransactionDate());
            if (!spendingByCategoryAndMonth.containsKey(transactionMonth)) continue;
            
            if (transaction.getType() == CategoryType.EXPENSE) {
                String categoryName = transaction.getTransactionCategory() != null 
                        ? transaction.getTransactionCategory().getName() : "Uncategorized";
                
                // Update category spending
                Map<String, Double> monthCategories = spendingByCategoryAndMonth.get(transactionMonth);
                monthCategories.put(categoryName, 
                        monthCategories.getOrDefault(categoryName, 0.0) + transaction.getAmount());
                
                // Update total spending
                totalSpendingByMonth.put(transactionMonth, 
                        totalSpendingByMonth.get(transactionMonth) + transaction.getAmount());
            } else if (transaction.getType() == CategoryType.INCOME) {
                // Update total income
                totalIncomeByMonth.put(transactionMonth, 
                        totalIncomeByMonth.get(transactionMonth) + transaction.getAmount());
            }
        }
        
        // Calculate savings by month
        Map<YearMonth, Double> savingsByMonth = new LinkedHashMap<>();
        Map<YearMonth, Double> savingsRateByMonth = new LinkedHashMap<>();
        
        for (YearMonth month : last6Months) {
            Double monthlyIncome = totalIncomeByMonth.get(month);
            Double monthlySpending = totalSpendingByMonth.get(month);
            Double monthlySavings = monthlyIncome - monthlySpending;
            
            savingsByMonth.put(month, monthlySavings);
            savingsRateByMonth.put(month, monthlyIncome > 0 
                    ? (monthlySavings / monthlyIncome) * 100 : 0.0);
        }
        
        // Generate insights
        List<SpendingInsightDTO> insights = new ArrayList<>();
        
        // 1. Overall spending trend insight
        Double currentMonthSpending = totalSpendingByMonth.get(currentMonth);
        Double previousMonthSpending = totalSpendingByMonth.get(previousMonth);
        
        if (previousMonthSpending > 0) {
            Double percentChange = ((currentMonthSpending - previousMonthSpending) / previousMonthSpending) * 100;
            
            if (Math.abs(percentChange) >= 5) { // Only show if change is significant (≥5%)
                String message = percentChange > 0 
                        ? String.format("You spent %.1f%% more overall this month compared to last month", percentChange)
                        : String.format("You spent %.1f%% less overall this month compared to last month", Math.abs(percentChange));
                        
                String insightType = percentChange > 0 ? "SPENDING_INCREASE" : "SPENDING_DECREASE";
                int priority = percentChange > 0 ? 2 : 1; // Spending decrease is higher priority (good news)
                
                insights.add(SpendingInsightDTO.builder()
                        .message(message)
                        .percentageChange(percentChange)
                        .amount(currentMonthSpending)
                        .comparisonPeriod("last month")
                        .insightType(insightType)
                        .priority(priority)
                        .build());
            }
        }
        
        // 2. Category-specific insights
        Map<String, Double> categoryChanges = new HashMap<>();
        Set<String> allCategories = new HashSet<>();
        
        // Collect all categories from current and previous month
        spendingByCategoryAndMonth.get(currentMonth).keySet().forEach(allCategories::add);
        spendingByCategoryAndMonth.get(previousMonth).keySet().forEach(allCategories::add);
        
        // Calculate percentage changes for each category
        for (String category : allCategories) {
            Double currentAmount = spendingByCategoryAndMonth.get(currentMonth).getOrDefault(category, 0.0);
            Double previousAmount = spendingByCategoryAndMonth.get(previousMonth).getOrDefault(category, 0.0);
            
            // Only consider categories with meaningful spending (> $10) in at least one month
            if (Math.max(currentAmount, previousAmount) >= 10) {
                Double percentChange = previousAmount > 0 
                        ? ((currentAmount - previousAmount) / previousAmount) * 100
                        : (currentAmount > 0 ? 100.0 : 0.0); // New spending = 100% increase
                        
                categoryChanges.put(category, percentChange);
            }
        }
        
        // Find top categories with significant changes
        List<Map.Entry<String, Double>> significantChanges = categoryChanges.entrySet().stream()
                .filter(entry -> Math.abs(entry.getValue()) >= 20) // Only significant changes (≥20%)
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3) // Top 3 changes
                .collect(Collectors.toList());
                
        // Generate insights for each significant category change
        for (Map.Entry<String, Double> entry : significantChanges) {
            String category = entry.getKey();
            Double percentChange = entry.getValue();
            Double currentAmount = spendingByCategoryAndMonth.get(currentMonth).getOrDefault(category, 0.0);
            
            String message = percentChange > 0 
                    ? String.format("You spent %.1f%% more on %s this month", percentChange, category)
                    : String.format("You spent %.1f%% less on %s this month", Math.abs(percentChange), category);
                    
            String insightType = percentChange > 0 ? "CATEGORY_INCREASE" : "CATEGORY_DECREASE";
            int priority = Math.abs(percentChange) > 50 ? 1 : 2; // Very large changes get higher priority
            
            insights.add(SpendingInsightDTO.builder()
                    .message(message)
                    .category(category)
                    .percentageChange(percentChange)
                    .amount(currentAmount)
                    .comparisonPeriod("last month")
                    .insightType(insightType)
                    .priority(priority)
                    .build());
        }
        
        // 3. Savings trend insight
        Double currentMonthSavings = savingsByMonth.get(currentMonth);
        Double previousMonthSavings = savingsByMonth.get(previousMonth);
        
        // Calculate average monthly savings (excluding current month)
        Double averageMonthlySavings = last6Months.stream()
                .filter(month -> !month.equals(currentMonth))
                .mapToDouble(savingsByMonth::get)
                .average()
                .orElse(0.0);
                
        String savingsMessage = String.format("Your average monthly savings is $%.2f", averageMonthlySavings);
        insights.add(SpendingInsightDTO.builder()
                .message(savingsMessage)
                .amount(averageMonthlySavings)
                .insightType("SAVINGS_TREND")
                .priority(1) // High priority
                .build());
                
        // If current month savings differs significantly from average
        if (Math.abs(currentMonthSavings - averageMonthlySavings) > 100) { // $100 threshold
            Double savingsChange = ((currentMonthSavings - averageMonthlySavings) / Math.abs(averageMonthlySavings)) * 100;
            String comparisonMessage = savingsChange > 0 
                    ? String.format("You're saving %.1f%% more than your average this month", savingsChange)
                    : String.format("You're saving %.1f%% less than your average this month", Math.abs(savingsChange));
                    
            insights.add(SpendingInsightDTO.builder()
                    .message(comparisonMessage)
                    .percentageChange(savingsChange)
                    .amount(currentMonthSavings)
                    .comparisonPeriod("monthly average")
                    .insightType(savingsChange > 0 ? "SAVINGS_INCREASE" : "SAVINGS_DECREASE")
                    .priority(savingsChange > 0 ? 1 : 2)
                    .build());
        }
        
        // Identify top increasing and decreasing categories
        Map<String, Double> categoryChangeMap = new HashMap<>();
        
        for (String category : allCategories) {
            Double currentAmount = spendingByCategoryAndMonth.get(currentMonth).getOrDefault(category, 0.0);
            Double previousAmount = spendingByCategoryAndMonth.get(previousMonth).getOrDefault(category, 0.0);
            
            if (currentAmount > 0 && previousAmount > 0) {
                Double absoluteChange = currentAmount - previousAmount;
                categoryChangeMap.put(category, absoluteChange);
            }
        }
        
        // Sort categories by absolute change in spending
        List<Map.Entry<String, Double>> sortedCategoryChanges = new ArrayList<>(categoryChangeMap.entrySet());
        sortedCategoryChanges.sort(Map.Entry.<String, Double>comparingByValue().reversed());
        
        // Extract top increasing categories
        Map<String, Double> topIncreasingCategories = new LinkedHashMap<>();
        sortedCategoryChanges.stream()
                .filter(entry -> entry.getValue() > 0)
                .limit(5)
                .forEach(entry -> topIncreasingCategories.put(
                        entry.getKey(), entry.getValue()));
                        
        // Extract top decreasing categories
        Map<String, Double> topDecreasingCategories = new LinkedHashMap<>();
        sortedCategoryChanges.stream()
                .filter(entry -> entry.getValue() < 0)
                .sorted(Map.Entry.<String, Double>comparingByValue())
                .limit(5)
                .forEach(entry -> topDecreasingCategories.put(
                        entry.getKey(), Math.abs(entry.getValue())));
                        
        // Format the category spending by month for display
        Map<String, Map<String, Double>> categorySpendingByMonth = new LinkedHashMap<>();
        
        // Find top 5 categories by total spending across all months
        Map<String, Double> totalCategorySpending = new HashMap<>();
        for (YearMonth month : last6Months) {
            for (Map.Entry<String, Double> entry : spendingByCategoryAndMonth.get(month).entrySet()) {
                totalCategorySpending.put(entry.getKey(), 
                        totalCategorySpending.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
            }
        }
        
        List<String> topCategories = totalCategorySpending.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
                
        // For each top category, map monthly spending
        for (String category : topCategories) {
            Map<String, Double> monthlySpending = new LinkedHashMap<>();
            
            for (YearMonth month : last6Months) {
                String monthName = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.US) + " " + month.getYear();
                Double amount = spendingByCategoryAndMonth.get(month).getOrDefault(category, 0.0);
                monthlySpending.put(monthName, amount);
            }
            
            // Reverse the order to show oldest first
            Map<String, Double> reversedMonthlySpending = new LinkedHashMap<>();
            monthlySpending.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByKey())
                    .forEach(e -> reversedMonthlySpending.put(e.getKey(), e.getValue()));
                    
            categorySpendingByMonth.put(category, reversedMonthlySpending);
        }
        
        // Sort insights by priority 
        Collections.sort(insights, Comparator.comparing(SpendingInsightDTO::getPriority));
        
        // Return the response with all calculated data
        return SpendingTrendsResponse.builder()
                .insights(insights)
                .currentMonthSavings(currentMonthSavings)
                .previousMonthSavings(previousMonthSavings)
                .averageMonthlySavings(averageMonthlySavings)
                .savingsRateCurrentMonth(savingsRateByMonth.get(currentMonth))
                .savingsRateAverage(last6Months.stream()
                        .mapToDouble(savingsRateByMonth::get)
                        .average()
                        .orElse(0.0))
                .topIncreasingCategories(topIncreasingCategories)
                .topDecreasingCategories(topDecreasingCategories)
                .categorySpendingByMonth(categorySpendingByMonth)
                .totalSpendingCurrentMonth(totalSpendingByMonth.get(currentMonth))
                .totalSpendingPreviousMonth(totalSpendingByMonth.get(previousMonth))
                .percentageChangeInSpending(previousMonthSpending > 0 
                        ? ((currentMonthSpending - previousMonthSpending) / previousMonthSpending) * 100 
                        : 0.0)
                .averageMonthlySpending(totalSpendingByMonth.values().stream()
                        .mapToDouble(Double::doubleValue)
                        .average()
                        .orElse(0.0))
                .build();
    }

    // Helper methods
    
    private Double calculateTotalByType(List<Transaction> transactions, CategoryType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
    
    private Map<String, Double> getCategoryStatistics(List<Transaction> transactions, 
                                                     CategoryType type, 
                                                     int limit) {
        Map<String, Double> categorySums = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            if (transaction.getType() == type && transaction.getTransactionCategory() != null) {
                String categoryName = transaction.getTransactionCategory().getName();
                categorySums.put(categoryName, 
                        categorySums.getOrDefault(categoryName, 0.0) + transaction.getAmount());
            }
        }
        
        // Sort by amount and limit to top N
        return categorySums.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        HashMap::new
                ));
    }
    
    private Map<String, Double> getMonthlyBreakdown(List<Transaction> transactions, CategoryType type) {
        Map<String, Double> monthlyTotals = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (Transaction transaction : transactions) {
            if (transaction.getType() == type && transaction.getTransactionDate() != null) {
                String month = transaction.getTransactionDate().format(formatter);
                monthlyTotals.put(month, 
                        monthlyTotals.getOrDefault(month, 0.0) + transaction.getAmount());
            }
        }
        
        return monthlyTotals;
    }
    
    private List<TransactionSummaryDTO> getRecentTransactions(User user, int limit) {
        List<Transaction> transactions = transactionRepository.findByCreatedByOrderByTransactionDateDesc(
                user, PageRequest.of(0, limit));
                
        return transactions.stream()
                .map(this::mapToTransactionSummaryDTO)
                .collect(Collectors.toList());
    }
    
    private List<GoalSummaryDTO> getRecentGoals(User user, int limit) {
        List<Goal> goals = goalRepository.findByCreatedByOrderByCreatedAtDesc(
                user, PageRequest.of(0, limit));
                
        return goals.stream()
                .map(this::mapToGoalSummaryDTO)
                .collect(Collectors.toList());
    }
    
    private TransactionSummaryDTO mapToTransactionSummaryDTO(Transaction transaction) {
        return TransactionSummaryDTO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .categoryName(transaction.getTransactionCategory() != null 
                        ? transaction.getTransactionCategory().getName() : null)
                .transactionDate(transaction.getTransactionDate())
                .userName(transaction.getCreatedBy().getFullName())
                .build();
    }
    
    private GoalSummaryDTO mapToGoalSummaryDTO(Goal goal) {
        Double progressPercentage = 0.0;
        if (goal.getTargetAmount() > 0) {
            progressPercentage = (goal.getCurrentAmount() / goal.getTargetAmount()) * 100;
        }
        
        return GoalSummaryDTO.builder()
                .id(goal.getId())
                .name(goal.getName())
                .description(goal.getDescription())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .progressPercentage(progressPercentage)
                .status(goal.getStatus())
                .targetDate(goal.getTargetDate())
                .categoryName(goal.getCategory() != null ? goal.getCategory().getName() : null)
                .userName(goal.getCreatedBy().getFullName())
                .build();
    }
} 