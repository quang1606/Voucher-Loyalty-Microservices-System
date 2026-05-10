package com.example.loyaltyservice.repository;

import com.example.loyaltyservice.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPointRepository extends JpaRepository<CustomerEntity, Long> {
    
}