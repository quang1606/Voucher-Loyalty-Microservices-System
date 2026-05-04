package com.example.loyaltyservice.entity;

import com.example.loyaltyservice.constant.CustomerTier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_points")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPointEntity {

    @Id
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "total_points", nullable = false)
    private Long totalPoints = 0L;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_tier", nullable = false)
    private CustomerTier currentTier = CustomerTier.SILVER;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}