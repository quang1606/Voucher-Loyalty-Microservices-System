package com.example.customerservice.repository;

import com.example.customerservice.entity.RetryableKafkaMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RetryableKafkaMessageRepository extends JpaRepository<RetryableKafkaMessage, Long> {
    
}