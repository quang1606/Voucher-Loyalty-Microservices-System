package com.example.voucherservice;

import com.example.voucherservice.constant.CreatorType;
import com.example.voucherservice.constant.RequestMode;
import com.example.voucherservice.constant.RequestStatus;
import com.example.voucherservice.constant.VoucherPurpose;
import com.example.voucherservice.entity.VoucherRequestEntity;
import com.example.voucherservice.repository.VoucherRequestRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=none"
})
@Rollback(false)
class DataSeederTest {

    @Autowired
    private VoucherRequestRepository voucherRequestRepository;

    private final Random random = new Random();

    private final String[] storeNames = {"Circle K", "FamilyMart", "GS25", "MiniStop", "7-Eleven"};
    private final RequestMode[] requestModes = {RequestMode.SINGLE, RequestMode.EXCEL};
    private final CreatorType[] creatorTypes = {CreatorType.PARTNER, CreatorType.SYSTEM};
    private final VoucherPurpose[] purposes = {VoucherPurpose.REWARD, VoucherPurpose.HUNT};
    private final RequestStatus[] allStatuses = {
        RequestStatus.FINISH, RequestStatus.APPROVED, RequestStatus.PENDING_APPROVE,
        RequestStatus.DRAFT, RequestStatus.REJECTED, RequestStatus.CANCELLED,
        RequestStatus.PROCESSING, RequestStatus.FAILED
    };

    @Test
    void seedVoucherRequests() {
        List<VoucherRequestEntity> requests = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            VoucherRequestEntity entity = new VoucherRequestEntity();

            String reqId = "REQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
            RequestMode mode = requestModes[random.nextInt(requestModes.length)];
            CreatorType creatorType = creatorTypes[random.nextInt(creatorTypes.length)];
            VoucherPurpose purpose = purposes[random.nextInt(purposes.length)];
            RequestStatus status = allStatuses[random.nextInt(allStatuses.length)];
            String storeName = storeNames[random.nextInt(storeNames.length)];

            LocalDateTime createdTime = LocalDateTime.now()
                    .minusDays(random.nextInt(30) + 1)
                    .minusHours(random.nextInt(24));
            LocalDateTime updatedTime = createdTime.plusHours(random.nextInt(48) + 1);

            String fileName = mode == RequestMode.EXCEL ? "voucher_batch_" + (i + 1) + ".xlsx" : null;
            String reason = status == RequestStatus.REJECTED ? "Không đủ điều kiện phát hành" : null;
            String createdBy = "user_" + (random.nextInt(5) + 1);

            entity.setRequestId(reqId);
            entity.setRequestMode(mode);
            entity.setCreatorType(creatorType);
            entity.setVoucherPurpose(purpose);
            entity.setFileName(fileName);
            entity.setStatus(status);
            entity.setReason(reason);
            entity.setCreatedTime(createdTime);
            entity.setCreatedBy(createdBy);
            entity.setUpdatedTime(updatedTime);
            entity.setUpdatedBy(createdBy);
            entity.setStoreName(storeName);

            if (status == RequestStatus.APPROVED || status == RequestStatus.FINISH) {
                entity.setConfirmedTime(updatedTime);
                entity.setConfirmedBy("admin");
            }

            requests.add(entity);
        }

        voucherRequestRepository.saveAll(requests);
        System.out.println("✅ Seeded " + requests.size() + " voucher requests into voucher_db");
    }
}
