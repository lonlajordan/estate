package com.estate.repository;

import com.estate.domain.entity.Lease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface LeaseRepository extends JpaRepository<Lease, Long>, JpaSpecificationExecutor<Lease> {
    List<Lease> findAllByEndDateBeforeAndLastRememberDateNull(LocalDate date);
    Page<Lease> findAllByPaymentStudentUserIdOrderByCreationDateDesc(Long userId, Pageable pageable);

    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteAllByPaymentStandingId(long standingId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Lease l SET l.housing = null WHERE l.housing.id = ?1")
    void setHousingToNullByHousingId(long housingId);

    Page<Lease> findAllByOrderByCreationDateDesc(PageRequest pageable);
}
