package com.budget_tracker.tracker.budget_tracker.services.export;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.budget_tracker.tracker.budget_tracker.entity.Budget;
import com.budget_tracker.tracker.budget_tracker.entity.Goal;
import com.budget_tracker.tracker.budget_tracker.entity.Transaction;
import com.budget_tracker.tracker.budget_tracker.entity.User;
import com.budget_tracker.tracker.budget_tracker.exception.common.NotFoundException;
import com.budget_tracker.tracker.budget_tracker.repositories.BudgetRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.GoalRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.TransactionRepository;
import com.budget_tracker.tracker.budget_tracker.repositories.UserRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Export user's transactions to CSV format
     * 
     * @param userEmail Email of the user
     * @return CSV content as ByteArrayInputStream
     */
    public ByteArrayInputStream exportTransactionsToCSV(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Transaction> transactions = transactionRepository.findByCreatedByOrderByTransactionDateDesc(user, pageable);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(
                     new OutputStreamWriter(out, StandardCharsets.UTF_8),
                     CSVFormat.DEFAULT.builder()
                             .setHeader("ID", "Date", "Description", "Category", "Type", "Amount")
                             .build())) {
            
            for (Transaction transaction : transactions) {
                String date = transaction.getTransactionDate() != null 
                        ? transaction.getTransactionDate().format(DATE_FORMATTER) : "";
                String category = transaction.getTransactionCategory() != null 
                        ? transaction.getTransactionCategory().getName() : "";
                
                csvPrinter.printRecord(
                        transaction.getId(),
                        date,
                        transaction.getDescription(),
                        category,
                        transaction.getType(),
                        transaction.getAmount()
                );
            }
            
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export transactions to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Export user's budgets to CSV format
     * 
     * @param userEmail Email of the user
     * @return CSV content as ByteArrayInputStream
     */
    public ByteArrayInputStream exportBudgetsToCSV(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        List<Budget> budgets = budgetRepository.findAllByCreatedBy(user);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(
                     new OutputStreamWriter(out, StandardCharsets.UTF_8),
                     CSVFormat.DEFAULT.builder()
                             .setHeader("ID", "Name", "Description", "Category", "Amount")
                             .build())) {
            
            for (Budget budget : budgets) {
                String category = budget.getCategory() != null 
                        ? budget.getCategory().getName() : "";
                
                csvPrinter.printRecord(
                        budget.getId(),
                        budget.getName(),
                        budget.getDescription(),
                        category,
                        budget.getAmount()
                );
            }
            
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export budgets to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Export user's goals to CSV format
     * 
     * @param userEmail Email of the user
     * @return CSV content as ByteArrayInputStream
     */
    public ByteArrayInputStream exportGoalsToCSV(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        List<Goal> goals = goalRepository.findAllByCreatedBy(user);
        
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(
                     new OutputStreamWriter(out, StandardCharsets.UTF_8),
                     CSVFormat.DEFAULT.builder()
                             .setHeader("ID", "Name", "Description", "Target Amount", "Current Amount", 
                                        "Target Date", "Status", "Category")
                             .build())) {
            
            for (Goal goal : goals) {
                String targetDate = goal.getTargetDate() != null 
                        ? goal.getTargetDate().format(DATE_FORMATTER) : "";
                String category = goal.getCategory() != null 
                        ? goal.getCategory().getName() : "";
                
                csvPrinter.printRecord(
                        goal.getId(),
                        goal.getName(),
                        goal.getDescription(),
                        goal.getTargetAmount(),
                        goal.getCurrentAmount(),
                        targetDate,
                        goal.getStatus(),
                        category
                );
            }
            
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to export goals to CSV: " + e.getMessage());
        }
    }
    
    /**
     * Export user's transactions to PDF
     * 
     * @param userEmail Email of the user
     * @return PDF content as ByteArrayInputStream
     */
    public ByteArrayInputStream exportTransactionsToPDF(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        List<Transaction> transactions = transactionRepository.findByCreatedByOrderByTransactionDateDesc(user, pageable);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Transactions Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Add space
            
            // Add timestamp
            Font timestampFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph timestamp = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    timestampFont);
            timestamp.setAlignment(Element.ALIGN_RIGHT);
            document.add(timestamp);
            document.add(new Paragraph(" ")); // Add space
            
            // Create table
            PdfPTable table = new PdfPTable(5); // 5 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 5, 3, 2, 2});
            
            // Add table headers
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Stream.of("Date", "Description", "Category", "Type", "Amount")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setPhrase(new Phrase(columnTitle, headerFont));
                        table.addCell(header);
                    });
            
            // Add data rows
            for (Transaction transaction : transactions) {
                String date = transaction.getTransactionDate() != null 
                        ? transaction.getTransactionDate().format(DATE_FORMATTER) : "";
                String category = transaction.getTransactionCategory() != null 
                        ? transaction.getTransactionCategory().getName() : "";
                
                table.addCell(date);
                table.addCell(transaction.getDescription());
                table.addCell(category);
                table.addCell(transaction.getType().toString());
                table.addCell(String.format("$%.2f", transaction.getAmount()));
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayInputStream(out.toByteArray());
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to export transactions to PDF: " + e.getMessage());
        }
    }
    
    /**
     * Export user's budgets to PDF
     * 
     * @param userEmail Email of the user
     * @return PDF content as ByteArrayInputStream
     */
    public ByteArrayInputStream exportBudgetsToPDF(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        List<Budget> budgets = budgetRepository.findAllByCreatedBy(user);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Budgets Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Add space
            
            // Add timestamp
            Font timestampFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph timestamp = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    timestampFont);
            timestamp.setAlignment(Element.ALIGN_RIGHT);
            document.add(timestamp);
            document.add(new Paragraph(" ")); // Add space
            
            // Create table
            PdfPTable table = new PdfPTable(4); // 4 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{5, 8, 4, 3});
            
            // Add table headers
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Stream.of("Name", "Description", "Category", "Amount")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setPhrase(new Phrase(columnTitle, headerFont));
                        table.addCell(header);
                    });
            
            // Add data rows
            for (Budget budget : budgets) {
                String category = budget.getCategory() != null 
                        ? budget.getCategory().getName() : "";
                
                table.addCell(budget.getName());
                table.addCell(budget.getDescription());
                table.addCell(category);
                table.addCell(String.format("$%.2f", budget.getAmount()));
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayInputStream(out.toByteArray());
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to export budgets to PDF: " + e.getMessage());
        }
    }
    
    /**
     * Export user's goals to PDF
     * 
     * @param userEmail Email of the user
     * @return PDF content as ByteArrayInputStream
     */
    public ByteArrayInputStream exportGoalsToPDF(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        List<Goal> goals = goalRepository.findAllByCreatedBy(user);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
            Paragraph title = new Paragraph("Financial Goals Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Add space
            
            // Add timestamp
            Font timestampFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
            Paragraph timestamp = new Paragraph("Generated on: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    timestampFont);
            timestamp.setAlignment(Element.ALIGN_RIGHT);
            document.add(timestamp);
            document.add(new Paragraph(" ")); // Add space
            
            // Create table
            PdfPTable table = new PdfPTable(6); // 6 columns
            table.setWidthPercentage(100);
            table.setWidths(new float[]{5, 5, 3, 3, 3, 3});
            
            // Add table headers
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
            Stream.of("Name", "Description", "Target ($)", "Current ($)", "Progress (%)", "Status")
                    .forEach(columnTitle -> {
                        PdfPCell header = new PdfPCell();
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setBorderWidth(1);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        header.setPhrase(new Phrase(columnTitle, headerFont));
                        table.addCell(header);
                    });
            
            // Add data rows
            for (Goal goal : goals) {
                double progressPercentage = 0;
                if (goal.getTargetAmount() > 0) {
                    progressPercentage = (goal.getCurrentAmount() / goal.getTargetAmount()) * 100;
                }
                
                table.addCell(goal.getName());
                table.addCell(goal.getDescription());
                table.addCell(String.format("$%.2f", goal.getTargetAmount()));
                table.addCell(String.format("$%.2f", goal.getCurrentAmount()));
                table.addCell(String.format("%.1f%%", progressPercentage));
                table.addCell(goal.getStatus().toString());
            }
            
            document.add(table);
            document.close();
            
            return new ByteArrayInputStream(out.toByteArray());
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to export goals to PDF: " + e.getMessage());
        }
    }
} 