package com.santosh.bankingsystem.userstatement.service.impl;

import com.santosh.bankingsystem.cloudconfiguration.PdfGeneratorService;
import com.santosh.bankingsystem.cloudconfiguration.S3Service;
import com.santosh.bankingsystem.entity.User;
import com.santosh.bankingsystem.exceptions.ResourceNotFoundException;
import com.santosh.bankingsystem.repository.AccountRepository;
import com.santosh.bankingsystem.service.UserService;
import com.santosh.bankingsystem.userstatement.service.StatementService;
import com.santosh.bankingsystem.usertransaction.dto.TransactionResponseDTO;
import com.santosh.bankingsystem.usertransaction.entity.Transaction;
import com.santosh.bankingsystem.usertransaction.repository.TransactionRepository;
import com.santosh.bankingsystem.usertransaction.service.TransactionService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatementServiceImpl implements StatementService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PdfGeneratorService pdfGenerator;

    @Autowired
    private S3Service s3Service;


    @Override
    public List<TransactionResponseDTO> getAllStatements(Long accountId) {
        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<TransactionResponseDTO> getStatementsByDateRange(Long accountId, LocalDateTime start, LocalDateTime end) {
        return transactionRepository.findByAccountIdAndTimestampBetween(accountId, start, end)
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public List<TransactionResponseDTO> getStatementsByType(Long accountId, String type) {
        return transactionRepository.findByAccountIdAndTransactionType(accountId, type.toUpperCase())
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    public boolean writeStatementToCsv(Long accountId, HttpServletResponse response) throws IOException {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        if (transactions.isEmpty()) {
            return false;
        }

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=statement.csv");

        PrintWriter writer = response.getWriter();
        writer.println("TransactionId,AccountId,Type,Amount,Date,Description");

        for (Transaction txn : transactions) {
            writer.printf("%d,%d,%s,%.2f,%s%n",
                    txn.getId(),
                    txn.getAccount().getId(),
                    txn.getTransactionType(),
                    txn.getAmount(),
                   // txn.getTransactionDate(),
                    txn.getDescription()
            );
        }

        writer.flush();
        return true;
    }

    @Override
    public String generateAndUploadStatement(Long userId) throws IOException {
        User user = userService.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with id:"+userId));


        List<Transaction> transactions = transactionService.getAllTransactionsForUser(userId);

        // Generate PDF as ByteArrayOutputStream
        byte[] pdfBytes = pdfGenerator.generateStatementPdf(transactions, user);

        // Convert to InputStream for upload
       // ByteArrayInputStream inputStream = new ByteArrayInputStream(pdfStream.toByteArray());

        // Generate a unique file name
        String fileName = "statements/" + user.getUsername() + "-statement-" + System.currentTimeMillis() + ".pdf";

        // Upload to S3
        String s3Url = s3Service.uploadFile(fileName, pdfBytes, "application/pdf");

        return s3Url; // Return the URL in controller
    }

    private TransactionResponseDTO convertToDTO(Transaction txn) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(txn.getId());
        dto.setTransactionType(txn.getTransactionType());
        dto.setAmount(txn.getAmount());
        dto.setDescription(txn.getDescription());
        dto.setTimestamp(txn.getTimestamp());
        dto.setAccountId(txn.getAccount().getId());
        return dto;
    }
}