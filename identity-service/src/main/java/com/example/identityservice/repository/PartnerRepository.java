package com.example.identityservice.repository;

import com.example.identityservice.entity.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Optional<Partner> findByUserId(UUID userId);
    void deleteByUserId(UUID userId);
    boolean existsByPhone(String phone);

  boolean existsByStoreName(String storeName);

  Optional<Partner> findByStoreName(String storeName);

  Optional<Object> findStoreNameByUserId(UUID uuid);
}
