package com.example.identityservice.repository;

import com.example.identityservice.entity.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Optional<Merchant> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    boolean existsByPhone(String phone);
}
