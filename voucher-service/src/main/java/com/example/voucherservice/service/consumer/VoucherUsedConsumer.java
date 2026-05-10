package com.example.voucherservice.service.consumer;

import com.example.common.BaseException;
import com.example.voucherservice.constant.VoucherStatus;
import com.example.voucherservice.dto.event.VoucherUsedEvent;
import com.example.voucherservice.entity.VoucherDetailEntity;
import com.example.voucherservice.repository.VoucherRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherUsedConsumer {

    private final VoucherRepository voucherRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "voucher-used-topic",
            groupId = "voucher-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleVoucherUsed(String message) {
        try {
            log.info("Received voucher used event: {}", message);
            
            VoucherUsedEvent event = objectMapper.readValue(message, VoucherUsedEvent.class);
            
            VoucherDetailEntity voucher = voucherRepository.findById(event.getVoucherId())
                    .orElseThrow(() -> BaseException.builder()
                            .httpStatus(HttpStatus.NOT_FOUND)
                            .errorCode("VOUCHER_NOT_FOUND")
                            .description("Voucher not found: " + event.getVoucherId())
                            .build());
            
            // Reduce available stock in database
            int currentStock = voucher.getAvailableStock();
            if (currentStock > 0) {
                voucher.setAvailableStock(currentStock - 1);
                if(currentStock-1==0){
                    voucher.setStatus(VoucherStatus.OUT_OF_STOCK);
                }
                voucherRepository.save(voucher);
                
                log.info("Reduced voucher stock in DB - voucherId: {}, code: {}, newStock: {}", 
                        event.getVoucherId(), event.getVoucherCode(), voucher.getAvailableStock());
            } else {
                log.warn("Voucher already out of stock - voucherId: {}, code: {}", 
                        event.getVoucherId(), event.getVoucherCode());
            }
            
        } catch (Exception ex) {
            log.error("Failed to process voucher used event: {}", message, ex);
            throw BaseException.builder()
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .errorCode("VOUCHER_EVENT_PROCESSING_FAILED")
                    .description("Failed to process voucher used event: " + ex.getMessage())
                    .build();
        }
    }
}