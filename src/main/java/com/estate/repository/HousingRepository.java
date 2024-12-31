package com.estate.repository;

import com.estate.domain.entity.Housing;
import com.estate.domain.enumaration.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HousingRepository extends JpaRepository<Housing, Long>, JpaSpecificationExecutor<Housing> {
    List<Housing> findAllByOrderByNameAsc();
    List<Housing> findAllByAvailableAndActiveTrueOrderByNameAsc(boolean available);
    List<Housing> findAllByStandingIdAndActiveTrueOrderByNameAsc(long standingId);
    List<Housing> findAllByStandingIdAndAvailableAndActiveTrueOrderByNameAsc(long standingId, boolean available);

    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteAllByStandingId(long standingId);
}
