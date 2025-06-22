package com.santosh.bankingsystem.userstatement.service;

import com.santosh.bankingsystem.usertransaction.dto.TransactionResponseDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.List;

public interface StatementService {
    List<TransactionResponseDTO> getAllStatements(Long accountId);
    List<TransactionResponseDTO> getStatementsByDateRange(Long accountId, LocalDateTime start, LocalDateTime end);
    List<TransactionResponseDTO> getStatementsByType(Long accountId, String type);
    boolean writeStatementToCsv(Long accountId, HttpServletResponse response) throws IOException;
    String generateAndUploadStatement(Long userId) throws IOException;
}