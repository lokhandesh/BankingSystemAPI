package com.santosh.bankingsystem.userstatement.controller;

import com.santosh.bankingsystem.cloudconfiguration.PdfGeneratorService;
import com.santosh.bankingsystem.cloudconfiguration.S3Service;
import com.santosh.bankingsystem.cloudemailservice.EmailService;
import com.santosh.bankingsystem.dto.ApiResponse;
import com.santosh.bankingsystem.entity.User;
import com.santosh.bankingsystem.exceptions.ResourceNotFoundException;
import com.santosh.bankingsystem.service.UserService;
import com.santosh.bankingsystem.userstatement.service.StatementService;
import com.santosh.bankingsystem.usertransaction.dto.TransactionResponseDTO;
import com.santosh.bankingsystem.usertransaction.entity.Transaction;
import com.santosh.bankingsystem.usertransaction.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class StatementController {

    @Autowired
    private StatementService statementService;

    @Autowired
    private TransactionService transactionService;

    private final UserService userService;
   // private final TransactionService transactionService;
    private final PdfGeneratorService pdfGeneratorService;
    private final S3Service s3Service;
    private final EmailService emailService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> getAllStatementsByAccount(@PathVariable Long accountId) {

        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByAccountId(accountId);
        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("No statements found for account ID: " + accountId);
        }

        ApiResponse<List<TransactionResponseDTO>> response = new ApiResponse<>(
                true, "Statements fetched successfully", transactions,null
        );
         return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}/date-range")
    public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> getStatementsByDateRange(
            @PathVariable Long accountId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
      //  return ResponseEntity.ok(statementService.getStatementsByDateRange(accountId, start, end));
        return ResponseEntity.ok(new ApiResponse<>(true,"Statement fetched successfully",statementService.getStatementsByDateRange(accountId, start, end),null));
    }

    @GetMapping("/download/{accountId}")
    public ResponseEntity<?> downloadTransactionStatement(@PathVariable Long accountId,
                                             HttpServletResponse response) throws IOException {
        boolean downloaded = statementService.writeStatementToCsv(accountId, response);

        if (!downloaded) {
            ApiResponse<String> apiResponse = new ApiResponse<>(false, "No transactions found", null, "ERR_NO_TXN");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
        }

        return null; // File has been written to response
    }

    @GetMapping("/account/{accountId}/type")
    public ResponseEntity<ApiResponse<List<TransactionResponseDTO>>> getStatementsByType(
            @PathVariable Long accountId,
            @RequestParam String type) {
       // return ResponseEntity.ok(statementService.getStatementsByType(accountId, type));
        return ResponseEntity.ok(new ApiResponse<>(true,"Statement Fetched Successfully",statementService.getStatementsByType(accountId, type),null));
    }

    @PostMapping("/email")
    public ResponseEntity<?> generateAndEmailStatement(@RequestParam Long userId) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Logged in user: " + auth.getName());
        User user = userService.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with id:"+userId));
        List<Transaction> transactions = transactionService.getAllTransactionsForUser(userId);

        byte[] pdfBytes = pdfGeneratorService.generateStatementPdf(transactions, user);
        String key = "statements/" + user.getUsername() + "_statement_" + System.currentTimeMillis() + ".pdf";

        String s3Url = s3Service.uploadFile(key, pdfBytes, "application/pdf");

        String emailBody = "Hi " + user.getUsername() + ",\n\nHere is your bank statement:\n" + s3Url;
        emailService.sendEmail(user.getEmail(), "Your Bank Statement", emailBody);

        return ResponseEntity.ok(new ApiResponse<>(true, "Statement generated and emailed", s3Url,null));

    }

    @GetMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String,String>>> uploadStatement(@RequestParam Long userId) {
        try {
            String fileUrl = statementService.generateAndUploadStatement(userId);
            Map<String,String> data = new HashMap<>();
            data.put("fileUrl",fileUrl);
            return ResponseEntity.ok(new ApiResponse<>(true, "Statement generated and uploaded", data,null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Failed to generate/upload statement", null, "ERR_UPLOAD"));
        }
    }

}