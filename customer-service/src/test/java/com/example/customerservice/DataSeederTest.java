package com.example.customerservice;

import com.example.customerservice.entity.Transaction;
import com.example.customerservice.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private TransactionRepository transactionRepository;

    private final Random random = new Random();

    private final Long[] customerIds = {1L, 2L, 3L, 4L, 5L};

    private final String[] voucherCodes = {
        "VCH-CED5E7B74F", "VCH-ABC1234567", "VCH-DEF9876543",
        "VCH-GHI5551234", "VCH-JKL7778899"
    };

    @Test
    void seedTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            Transaction txn = new Transaction();

            String txnId = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
            Long customerId = customerIds[random.nextInt(customerIds.length)];

            BigDecimal originalAmount = BigDecimal.valueOf((random.nextInt(451) + 50) * 1000L);

            boolean hasVoucher = random.nextBoolean();
            BigDecimal discountAmount = BigDecimal.ZERO;
            Long voucherId = null;
            String voucherCode = null;
            String requestId = null;

            if (hasVoucher) {
                int discountPercent = random.nextInt(26) + 5;
                discountAmount = originalAmount.multiply(BigDecimal.valueOf(discountPercent))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                voucherId = (long) (random.nextInt(5) + 1);
                voucherCode = voucherCodes[random.nextInt(voucherCodes.length)];
                requestId = String.valueOf(random.nextInt(20) + 1);
            }

            BigDecimal finalAmount = originalAmount.subtract(discountAmount);
            int pointsEarned = originalAmount.divide(BigDecimal.valueOf(1000), RoundingMode.DOWN).intValue();

            LocalDateTime createdAt = LocalDateTime.now()
                    .minusDays(random.nextInt(30))
                    .minusHours(random.nextInt(24))
                    .minusMinutes(random.nextInt(60));

            txn.setTransactionId(txnId);
            txn.setCustomerId(customerId);
            txn.setInvoiceId((long) (random.nextInt(50) + 1));
            txn.setVoucherId(voucherId);
            txn.setVoucherCode(voucherCode);
            txn.setRequestId(requestId);
            txn.setOriginalAmount(originalAmount);
            txn.setDiscountAmount(discountAmount);
            txn.setFinalAmount(finalAmount);
            txn.setPointsEarned(pointsEarned);
            txn.setStatus(Transaction.TransactionStatus.SUCCESS);
            txn.setCreatedAt(createdAt);

            transactions.add(txn);
        }

        transactionRepository.saveAll(transactions);
        System.out.println("✅ Seeded " + transactions.size() + " transactions into customer_db");
    }
}
