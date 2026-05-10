package com.example.voucherservice.scheduler;

import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.repository.VoucherRepository;
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
public class VoucherScheduler {

    private static final int BATCH_SIZE = 200;
    private final VoucherRepository voucherRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void executeVoucherStatusJob() {
        log.info("=== Voucher Status Scheduler START ===");
        activateVouchers();
        expireVouchers();
        log.info("=== Voucher Status Scheduler END ===");
    }

    @Transactional
    public void activateVouchers() {
        log.info("--- Activating vouchers ---");
        LocalDateTime today = LocalDate.now().atStartOfDay();
        Long nextId = 0L;
        int batchNumber = 0;
        int totalProcessed = 0;

        while (true) {
            List<VoucherDetailEntity> batch = voucherRepository
                    .findByStatusAndStartDateLessThanEqualAndIdGreaterThanOrderByIdAsc(
                            VoucherStatus.APPROVED, today, nextId,
                            PageRequest.of(0, BATCH_SIZE));

            if (batch.isEmpty()) break;

            batchNumber++;
            for (VoucherDetailEntity voucher : batch) {
                voucher.setStatus(VoucherStatus.ACTIVE);
            }
            voucherRepository.saveAll(batch);

            totalProcessed += batch.size();
            nextId = batch.get(batch.size() - 1).getId();
            log.info("Activate batch #{}: {} items, totalProcessed: {}", batchNumber, batch.size(), totalProcessed);

            if (batch.size() < BATCH_SIZE) break;
        }
        log.info("Voucher activation completed - total: {}", totalProcessed);
    }

    @Transactional
    public void expireVouchers() {
        log.info("--- Expiring vouchers ---");
        LocalDateTime today = LocalDate.now().atStartOfDay();
        Long nextId = 0L;
        int batchNumber = 0;
        int totalProcessed = 0;

        while (true) {
            List<VoucherDetailEntity> batch = voucherRepository
                    .findByStatusAndEndDateLessThanAndIdGreaterThanOrderByIdAsc(
                            VoucherStatus.ACTIVE, today, nextId,
                            PageRequest.of(0, BATCH_SIZE));

            if (batch.isEmpty()) break;

            batchNumber++;
            for (VoucherDetailEntity voucher : batch) {
                voucher.setStatus(VoucherStatus.EXPIRED);
            }
            voucherRepository.saveAll(batch);

            totalProcessed += batch.size();
            nextId = batch.get(batch.size() - 1).getId();
            log.info("Expire batch #{}: {} items, totalProcessed: {}", batchNumber, batch.size(), totalProcessed);

            if (batch.size() < BATCH_SIZE) break;
        }
        log.info("Voucher expiration completed - total: {}", totalProcessed);
    }
}
