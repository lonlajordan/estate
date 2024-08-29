package com.estate.repository;

import com.estate.domain.entity.Lease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface LeaseRepository extends JpaRepository<Lease, Long> {
    List<Lease> findAllByEndDate(LocalDate endDate);

    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteAllByPaymentStandingId(long standingId);
}
