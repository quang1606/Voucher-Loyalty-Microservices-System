package com.example.voucherservice.repository;

import com.example.voucherservice.entity.MockInvoiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MockInvoiceRepository extends JpaRepository<MockInvoiceEntity, Long> {
    
    Page<MockInvoiceEntity> findByNameStore(String nameStore, Pageable pageable);

    @Query("SELECT m FROM MockInvoiceEntity m WHERE " +
            "(:nameStore IS NULL OR m.nameStore LIKE :nameStore) AND " +
            "(:title IS NULL OR m.title LIKE :title)")
    Page<MockInvoiceEntity> findByFilters(@Param("nameStore") String nameStore,
                                          @Param("title") String title,
                                          Pageable pageable);
}