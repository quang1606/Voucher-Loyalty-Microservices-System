package com.example.loyaltyservice.repository;

import com.example.loyaltyservice.constant.MissionStatus;
import com.example.loyaltyservice.constant.TaskStatus;
import com.example.loyaltyservice.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface MissionRepository extends JpaRepository<MissionEntity, Long>,
    JpaSpecificationExecutor<MissionEntity> {

    List<MissionEntity> findByMissionStatusAndStatusAndStartDateLessThanEqualAndIdGreaterThanOrderByIdAsc(
            MissionStatus missionStatus, TaskStatus taskStatus, LocalDateTime startDate, Long id,
            org.springframework.data.domain.Pageable pageable);

    List<MissionEntity> findByMissionStatusAndEndDateLessThanAndIdGreaterThanOrderByIdAsc(
            MissionStatus missionStatus, LocalDateTime endDate, Long id,
            org.springframework.data.domain.Pageable pageable);

    List<MissionEntity> findByMissionStatusAndEndDateLessThanAndIdGreaterThanOrderByIdAsc(
            MissionStatus missionStatus, LocalDateTime endDate, Long id);

    void deleteAllByMissionStatusAndEndDateLessThan(MissionStatus missionStatus, LocalDateTime date);
}
