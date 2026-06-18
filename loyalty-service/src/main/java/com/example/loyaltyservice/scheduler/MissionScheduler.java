package com.example.loyaltyservice.scheduler;

import com.example.loyaltyservice.constant.MissionStatus;
import com.example.loyaltyservice.constant.TaskStatus;
import com.example.loyaltyservice.entity.MissionEntity;
import com.example.loyaltyservice.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MissionScheduler {

    private static final int BATCH_SIZE = 200;
    private final MissionRepository missionRepository;

    @Scheduled(cron = "0 27 2 * * *")    public void executeMissionStatusJob() {
        log.info("=== Mission Status Scheduler START ===");
        activateMissions();
        expireMissions();
        log.info("=== Mission Status Scheduler END ===");
    }

    @Transactional
    public void activateMissions() {
        log.info("--- Activating missions ---");
        LocalDateTime today = LocalDate.now().atStartOfDay();
        Long nextId = 0L;
        int batchNumber = 0;
        int totalProcessed = 0;

        while (true) {
            List<MissionEntity> batch = missionRepository
                    .findByMissionStatusAndStatusAndStartDateLessThanEqualAndIdGreaterThanOrderByIdAsc(
                            MissionStatus.APPROVED, TaskStatus.FINISH, today, nextId,
                            PageRequest.of(0, BATCH_SIZE));

            if (batch.isEmpty()) break;

            batchNumber++;
            for (MissionEntity mission : batch) {
                mission.setMissionStatus(MissionStatus.ACTIVE);
            }
            missionRepository.saveAll(batch);

            totalProcessed += batch.size();
            nextId = batch.get(batch.size() - 1).getId();
            log.info("Activate batch #{}: {} items, totalProcessed: {}", batchNumber, batch.size(), totalProcessed);

            if (batch.size() < BATCH_SIZE) break;
        }
        log.info("Activation completed - total: {}", totalProcessed);
    }

    @Transactional
    public void expireMissions() {
        log.info("--- Expiring missions ---");
        LocalDateTime today = LocalDateTime.now();
        Long nextId = 0L;
        int batchNumber = 0;
        int totalProcessed = 0;

        while (true) {
            List<MissionEntity> batch = missionRepository
                    .findByMissionStatusAndEndDateLessThanAndIdGreaterThanOrderByIdAsc(
                            MissionStatus.ACTIVE, today, nextId,
                            PageRequest.of(0, BATCH_SIZE));

            if (batch.isEmpty()) break;

            batchNumber++;
            for (MissionEntity mission : batch) {
                mission.setMissionStatus(MissionStatus.EXPIRED);
            }
            missionRepository.saveAll(batch);

            totalProcessed += batch.size();
            nextId = batch.get(batch.size() - 1).getId();
            log.info("Expire batch #{}: {} items, totalProcessed: {}", batchNumber, batch.size(), totalProcessed);

            if (batch.size() < BATCH_SIZE) break;
        }
        log.info("Expiration completed - total: {}", totalProcessed);
    }


}
