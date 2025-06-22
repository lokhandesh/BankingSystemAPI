package com.santosh.bankingsystem.usertransaction.service.impl;

import com.santosh.bankingsystem.entity.Account;
import com.santosh.bankingsystem.repository.AccountRepository;
import com.santosh.bankingsystem.usertransaction.dto.TransactionRequestDTO;
import com.santosh.bankingsystem.usertransaction.dto.TransactionResponseDTO;
import com.santosh.bankingsystem.usertransaction.entity.Transaction;
import com.santosh.bankingsystem.usertransaction.repository.TransactionRepository;
import com.santosh.bankingsystem.usertransaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public TransactionResponseDTO transferFunds(TransactionRequestDTO dto) {
        Account fromAccount = accountRepository.findById(dto.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountRepository.findById(dto.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Debit from sender
        fromAccount.setBalance(fromAccount.getBalance().subtract(dto.getAmount()));
        Transaction debit = new Transaction();
        debit.setTransactionType("DEBIT");
        debit.setAmount(dto.getAmount());
        debit.setDescription(dto.getDescription());
        debit.setTimestamp(LocalDateTime.now());
        debit.setAccount(fromAccount);
        transactionRepository.save(debit);

        // Credit to receiver
        toAccount.setBalance(toAccount.getBalance().add(dto.getAmount()));
        Transaction credit = new Transaction();
        credit.setTransactionType("CREDIT");
        credit.setAmount(dto.getAmount());
        credit.setDescription(dto.getDescription());
        credit.setTimestamp(LocalDateTime.now());
        credit.setAccount(toAccount);
        transactionRepository.save(credit);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        return mapToDto(transactionRepository.save(credit));
    }

    @Override
    public List<TransactionResponseDTO> getTransactionsByAccountId(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public TransactionResponseDTO deposit(TransactionRequestDTO dto) {
        Account account = accountRepository.findById(dto.getToAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(dto.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("CREDIT");
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription() != null ? dto.getDescription() : "Deposit");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAccount(account);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDto(savedTransaction);
    }

    @Override
    public TransactionResponseDTO withdraw(TransactionRequestDTO dto) {
        Account account = accountRepository.findById(dto.getFromAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(dto.getAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionType("DEBIT");
        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription() != null ? dto.getDescription() : "Withdrawal");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setAccount(account);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToDto(savedTransaction);
    }

    @Override
    public List<Transaction> getAllTransactionsForUser(Long userId) {
        return transactionRepository.findByAccount_User_Id(userId);
    }

    private TransactionResponseDTO mapToDto(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setTimestamp(transaction.getTimestamp());
        dto.setAccountId(transaction.getAccount().getId());
        return dto;
    }
}