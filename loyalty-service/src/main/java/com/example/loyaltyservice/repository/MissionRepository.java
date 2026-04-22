package com.example.loyaltyservice.repository;

import com.example.loyaltyservice.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MissionRepository extends JpaRepository<MissionEntity, Long>,
    JpaSpecificationExecutor<MissionEntity> {

}
