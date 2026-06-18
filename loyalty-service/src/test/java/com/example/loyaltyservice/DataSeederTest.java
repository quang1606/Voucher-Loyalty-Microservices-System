package com.example.loyaltyservice;

import com.example.loyaltyservice.constant.MissionStatus;
import com.example.loyaltyservice.constant.RewardType;
import com.example.loyaltyservice.constant.TargetType;
import com.example.loyaltyservice.constant.TaskStatus;
import com.example.loyaltyservice.entity.MissionEntity;
import com.example.loyaltyservice.repository.MissionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=none"
})
@Rollback(false)
class DataSeederTest {

    @Autowired
    private MissionRepository missionRepository;

    private final Random random = new Random();

    @Test
    void seedMissions() {
        List<MissionEntity> missions = new ArrayList<>();

        String[][] missionData = {
            {"Mua sắm 500K", "Hoàn thành giao dịch tổng 500.000 VND", "500000", "AMOUNT", "POINT", "500"},
            {"Mua 3 lần", "Thực hiện 3 giao dịch thành công", "3", "COUNT", "POINT", "100"},
            {"Chi tiêu 1 triệu", "Tổng chi tiêu đạt 1.000.000 VND", "1000000", "AMOUNT", "VOUCHER", "VCH-REWARD-01"},
            {"Mua 5 lần", "Thực hiện 5 giao dịch thành công", "5", "COUNT", "POINT", "200"},
            {"Chi tiêu 2 triệu", "Tổng chi tiêu đạt 2.000.000 VND", "2000000", "AMOUNT", "VOUCHER", "VCH-REWARD-02"},
            {"Thanh toán 10 lần", "Hoàn thành 10 giao dịch", "10", "COUNT", "POINT", "500"},
            {"Chi tiêu 5 triệu", "Tổng chi tiêu 5.000.000 VND", "5000000", "AMOUNT", "VOUCHER", "VCH-REWARD-03"},
            {"Mua 20 lần", "Thực hiện 20 giao dịch thành công", "20", "COUNT", "POINT", "1000"},
        };

        TaskStatus[] statuses = {TaskStatus.APPROVED, TaskStatus.APPROVED, TaskStatus.APPROVED,
                TaskStatus.PENDING_APPROVE, TaskStatus.INIT, TaskStatus.APPROVED, TaskStatus.APPROVED, TaskStatus.PENDING_APPROVE};
        MissionStatus[] missionStatuses = {MissionStatus.ACTIVE, MissionStatus.ACTIVE, MissionStatus.ACTIVE,
                MissionStatus.INACTIVE, MissionStatus.INACTIVE, MissionStatus.ACTIVE, MissionStatus.ACTIVE, MissionStatus.INACTIVE};

        for (int i = 0; i < missionData.length; i++) {
            String[] data = missionData[i];

            MissionEntity mission = new MissionEntity();
            mission.setRequestId(String.valueOf(random.nextInt(20) + 1));
            mission.setName(data[0]);
            mission.setDescription(data[1]);
            mission.setTargetValue(new BigDecimal(data[2]));
            mission.setTargetType(TargetType.valueOf(data[3]));
            mission.setRewardType(RewardType.valueOf(data[4]));
            mission.setRewardValue(data[5]);
            mission.setPartnerId((long) (random.nextInt(5) + 1));
            mission.setStartDate(LocalDateTime.now().minusDays(random.nextInt(15) + 1));
            mission.setEndDate(LocalDateTime.now().plusDays(random.nextInt(45) + 15));
            mission.setStatus(statuses[i]);
            mission.setMissionStatus(missionStatuses[i]);

            missions.add(mission);
        }

        missionRepository.saveAll(missions);
        System.out.println("✅ Seeded " + missions.size() + " missions into loyalty_db");
    }
}
