package com.example.identityservice.repository;

import com.example.identityservice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
}
