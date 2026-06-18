///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS net.datafaker:datafaker:2.1.0
//DEPS org.postgresql:postgresql:42.7.3

import net.datafaker.Faker;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Data Seeder - Generates test data for Transaction, MissionEntity, VoucherRequestEntity.
 *
 * Run with JBang:   jbang scripts/DataSeeder.java
 * Or run with Java: javac & java (need datafaker + postgresql driver in classpath)
 *
 * Config DB connection via environment variables:
 *   DB_HOST (default: localhost)
 *   DB_USERNAME (default: postgres)
 *   DB_PASSWORD (default: postgres)
 */
public class DataSeeder {

    static final Faker faker = new Faker(new Locale("vi"));
    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    // Customer IDs 1-5
    static final Long[] CUSTOMER_IDS = {1L, 2L, 3L, 4L, 5L};

    public static void main(String[] args) throws Exception {
        String host = System.getenv().getOrDefault("DB_HOST", "localhost");
        String username = System.getenv().getOrDefault("DB_USERNAME", "postgres");
        String password = System.getenv().getOrDefault("DB_PASSWORD", "postgres");

        // Seed Transactions in customer_db
        seedTransactions(host, username, password);

        // Seed Missions in loyalty_db
        seedMissions(host, username, password);

        // Seed VoucherRequests in voucher_db
        seedVoucherRequests(host, username, password);

        System.out.println("Data seeding completed!");
    }

    static void seedTransactions(String host, String username, String password) throws Exception {
        String url = "jdbc:postgresql://" + host + ":5432/customer_db";
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = """
                INSERT INTO transactions (transaction_id, customer_id, invoice_id, voucher_id, voucher_code, request_id,
                    original_amount, discount_amount, final_amount, points_earned, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (transaction_id) DO NOTHING
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // Generate 50 transactions
                for (int i = 0; i < 50; i++) {
                    Long customerId = CUSTOMER_IDS[faker.number().numberBetween(0, 5)];
                    String txnId = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

                    BigDecimal originalAmount = BigDecimal.valueOf(faker.number().numberBetween(50, 500) * 1000L);
                    boolean hasVoucher = faker.bool().bool();

                    BigDecimal discountAmount = BigDecimal.ZERO;
                    Long voucherId = null;
                    String voucherCode = null;
                    String requestId = null;

                    if (hasVoucher) {
                        // Random discount 5-30%
                        int discountPercent = faker.number().numberBetween(5, 31);
                        discountAmount = originalAmount.multiply(BigDecimal.valueOf(discountPercent))
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        voucherId = (long) faker.number().numberBetween(1, 10);
                        voucherCode = "VCH-" + UUID.randomUUID().toString().replace("-", "").substring(0, 11).toUpperCase();
                        requestId = String.valueOf(faker.number().numberBetween(1, 20));
                    }

                    BigDecimal finalAmount = originalAmount.subtract(discountAmount);
                    // Points based on original amount (1 point per 1000 VND)
                    int pointsEarned = originalAmount.divide(BigDecimal.valueOf(1000), RoundingMode.DOWN).intValue();

                    LocalDateTime createdAt = LocalDateTime.now()
                            .minusDays(faker.number().numberBetween(0, 30))
                            .minusHours(faker.number().numberBetween(0, 24))
                            .minusMinutes(faker.number().numberBetween(0, 60));

                    ps.setString(1, txnId);
                    ps.setLong(2, customerId);
                    ps.setLong(3, faker.number().numberBetween(1, 50));
                    if (voucherId != null) {
                        ps.setLong(4, voucherId);
                    } else {
                        ps.setNull(4, Types.BIGINT);
                    }
                    ps.setString(5, voucherCode);
                    ps.setString(6, requestId);
                    ps.setBigDecimal(7, originalAmount);
                    ps.setBigDecimal(8, discountAmount);
                    ps.setBigDecimal(9, finalAmount);
                    ps.setInt(10, pointsEarned);
                    ps.setString(11, "SUCCESS");
                    ps.setTimestamp(12, Timestamp.valueOf(createdAt));

                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                System.out.println("Inserted " + results.length + " transactions into customer_db");
            }
        }
    }

    static void seedMissions(String host, String username, String password) throws Exception {
        String url = "jdbc:postgresql://" + host + ":5432/loyalty_db";
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = """
                INSERT INTO tasks (request_id, name, description, target_value, target_type, partner_id,
                    reward_type, reward_value, start_date, end_date, status, mission_status, created_date, updated_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            String[][] missions = {
                {"Mua sắm 500K", "Hoàn thành giao dịch tổng 500.000 VND", "500000", "AMOUNT", "POINT", "500"},
                {"Mua 3 lần", "Thực hiện 3 giao dịch thành công", "3", "COUNT", "POINT", "100"},
                {"Chi tiêu 1 triệu", "Tổng chi tiêu đạt 1.000.000 VND", "1000000", "AMOUNT", "VOUCHER", "VCH-REWARD-01"},
                {"Mua 5 lần", "Thực hiện 5 giao dịch thành công", "5", "COUNT", "POINT", "200"},
                {"Chi tiêu 2 triệu", "Tổng chi tiêu đạt 2.000.000 VND", "2000000", "AMOUNT", "VOUCHER", "VCH-REWARD-02"},
            };

            String[] statuses = {"APPROVED", "APPROVED", "APPROVED", "PENDING_APPROVE", "INIT"};
            String[] missionStatuses = {"ACTIVE", "ACTIVE", "ACTIVE", "INACTIVE", "INACTIVE"};

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < missions.length; i++) {
                    String[] m = missions[i];
                    LocalDateTime startDate = LocalDateTime.now().minusDays(faker.number().numberBetween(1, 15));
                    LocalDateTime endDate = LocalDateTime.now().plusDays(faker.number().numberBetween(15, 60));

                    ps.setString(1, String.valueOf(faker.number().numberBetween(1, 20)));
                    ps.setString(2, m[0]);
                    ps.setString(3, m[1]);
                    ps.setBigDecimal(4, new BigDecimal(m[2]));
                    ps.setString(5, m[3]);
                    ps.setLong(6, faker.number().numberBetween(1, 5));
                    ps.setString(7, m[4]);
                    ps.setString(8, m[5]);
                    ps.setTimestamp(9, Timestamp.valueOf(startDate));
                    ps.setTimestamp(10, Timestamp.valueOf(endDate));
                    ps.setString(11, statuses[i]);
                    ps.setString(12, missionStatuses[i]);
                    ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));

                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                System.out.println("Inserted " + results.length + " missions into loyalty_db");
            }
        }
    }

    static void seedVoucherRequests(String host, String username, String password) throws Exception {
        String url = "jdbc:postgresql://" + host + ":5432/voucher_db";
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String sql = """
                INSERT INTO voucher_requests (request_id, request_mode, creator_type, voucher_purpose,
                    file_name, status, reason, created_time, created_by, updated_time, updated_by,
                    confirmed_time, confirmed_by, store_name)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            String[] storeNames = {"Circle K", "FamilyMart", "GS25", "MiniStop", "7-Eleven"};
            String[] requestModes = {"SINGLE", "EXCEL"};
            String[] creatorTypes = {"PARTNER", "SYSTEM"};
            String[] purposes = {"REWARD", "HUNT"};
            String[] reqStatuses = {"FINISH", "APPROVED", "PENDING_APPROVE", "DRAFT", "REJECTED"};

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < 20; i++) {
                    String reqId = "REQ-" + UUID.randomUUID().toString().replace("-", "").substring(0, 10).toUpperCase();
                    String mode = requestModes[faker.number().numberBetween(0, 2)];
                    String creatorType = creatorTypes[faker.number().numberBetween(0, 2)];
                    String purpose = purposes[faker.number().numberBetween(0, 2)];
                    String status = reqStatuses[faker.number().numberBetween(0, reqStatuses.length)];
                    String storeName = storeNames[faker.number().numberBetween(0, storeNames.length)];

                    LocalDateTime createdTime = LocalDateTime.now()
                            .minusDays(faker.number().numberBetween(1, 30))
                            .minusHours(faker.number().numberBetween(0, 24));
                    LocalDateTime updatedTime = createdTime.plusHours(faker.number().numberBetween(1, 48));

                    String fileName = mode.equals("EXCEL") ? "voucher_batch_" + (i + 1) + ".xlsx" : null;
                    String reason = status.equals("REJECTED") ? "Không đủ điều kiện" : null;
                    String createdBy = "user_" + faker.number().numberBetween(1, 5);

                    LocalDateTime confirmedTime = null;
                    String confirmedBy = null;
                    if (status.equals("APPROVED") || status.equals("FINISH")) {
                        confirmedTime = updatedTime;
                        confirmedBy = "admin";
                    }

                    ps.setString(1, reqId);
                    ps.setString(2, mode);
                    ps.setString(3, creatorType);
                    ps.setString(4, purpose);
                    ps.setString(5, fileName);
                    ps.setString(6, status);
                    ps.setString(7, reason);
                    ps.setTimestamp(8, Timestamp.valueOf(createdTime));
                    ps.setString(9, createdBy);
                    ps.setTimestamp(10, Timestamp.valueOf(updatedTime));
                    ps.setString(11, createdBy);
                    if (confirmedTime != null) {
                        ps.setTimestamp(12, Timestamp.valueOf(confirmedTime));
                        ps.setString(13, confirmedBy);
                    } else {
                        ps.setNull(12, Types.TIMESTAMP);
                        ps.setNull(13, Types.VARCHAR);
                    }
                    ps.setString(14, storeName);

                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                System.out.println("Inserted " + results.length + " voucher requests into voucher_db");
            }
        }
    }
}
