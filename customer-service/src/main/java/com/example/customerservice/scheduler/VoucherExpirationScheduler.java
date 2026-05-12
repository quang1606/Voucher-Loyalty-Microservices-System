package com.example.customerservice.scheduler;

import com.example.customerservice.constant.CustomerVoucherStatus;
import com.example.customerservice.entity.CustomerVoucher;
import com.example.customerservice.repository.CustomerVoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoucherExpirationScheduler {

    private static final int BATCH_SIZE = 500;

    private final CustomerVoucherRepository customerVoucherRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void expireVouchers() {
        log.info("VoucherExpirationScheduler - START");

        LocalDateTime now = LocalDateTime.now();
        Long nextId = 0L;
        int batchNumber = 0;
        int totalProcessed = 0;

        while (true) {
            List<CustomerVoucher> batch = customerVoucherRepository.findExpiredVouchers(
                    CustomerVoucherStatus.AVAILABLE, now, nextId, PageRequest.of(0, BATCH_SIZE));

            if (batch.isEmpty()) {
                break;
            }

            batchNumber++;

            for (CustomerVoucher voucher : batch) {
                voucher.setStatus(CustomerVoucherStatus.EXPIRED);
            }
            customerVoucherRepository.saveAll(batch);

            totalProcessed += batch.size();
            nextId = batch.get(batch.size() - 1).getId();

            log.info("VoucherExpirationScheduler - batch #{}: {} items expired, totalProcessed: {}",
                    batchNumber, batch.size(), totalProcessed);

            if (batch.size() < BATCH_SIZE) {
                break;
            }
        }

        log.info("VoucherExpirationScheduler - END. Total expired: {}, batches: {}", totalProcessed, batchNumber);
    }
}
