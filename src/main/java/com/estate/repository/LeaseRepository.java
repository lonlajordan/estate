package com.estate.repository;

import com.estate.domain.entity.Lease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaseRepository extends JpaRepository<Lease, Long> {
    List<Lease> findAllByEndDate(LocalDate endDate);
}
