package com.example.customerservice.repository;

import com.example.customerservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByCustomerId(Long customerId, Pageable pageable);

    Optional<Transaction> findByIdAndCustomerId(Long id, Long customerId);
}