package com.estate.repository;

import com.estate.domain.entity.Housing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HousingRepository extends JpaRepository<Housing, String>, JpaSpecificationExecutor<Housing> {
    List<Housing> findAllByOrderByNameAsc();
    List<Housing> findAllByAvailableAndActiveTrueOrderByNameAsc(boolean available);
    List<Housing> findAllByStandingIdAndActiveTrueOrderByNameAsc(String standingId);
    List<Housing> findAllByStandingIdAndAvailableAndActiveTrueOrderByNameAsc(String standingId, boolean available);

    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteAllByStandingId(String standingId);
}
