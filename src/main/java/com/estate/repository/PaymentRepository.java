package com.estate.repository;

import com.estate.domain.enumaration.Status;
import com.estate.domain.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    @Query("SELECT COALESCE(SUM(p.rent * p.months + p.caution + p.repair),0) FROM Payment p WHERE p.status = ?1")
    long sumAllAmount(Status status);
    Page<Payment> findAllByOrderByCreationDateDesc(Pageable pageable);
    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteAllByIdAndStatus(long id, Status status);
    long countAllByStatus(Status status);
}
