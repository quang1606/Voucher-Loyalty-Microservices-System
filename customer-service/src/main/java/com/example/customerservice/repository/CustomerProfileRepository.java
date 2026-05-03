package com.example.customerservice.repository;

import com.example.customerservice.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {
    Optional<CustomerProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
