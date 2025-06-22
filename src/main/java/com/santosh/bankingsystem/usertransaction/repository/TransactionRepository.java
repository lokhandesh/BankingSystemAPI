package com.santosh.bankingsystem.usertransaction.repository;

import com.santosh.bankingsystem.usertransaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccountIdAndTimestampBetween(Long accountId, LocalDateTime start, LocalDateTime end);
    List<Transaction> findByAccountIdAndTransactionType(Long accountId, String transactionType);
    //List<Transaction> findByFromAccount_UserIdOrToAccount_UserId(Long fromUserId, Long toUserId);
    List<Transaction> findByAccount_User_Id(Long userId);
}