package com.estate.repository;

import com.estate.domain.entity.Housing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface HousingRepository extends JpaRepository<Housing, Long>, JpaSpecificationExecutor<Housing> {
    List<Housing> findAllByOrderByNameAsc();
    List<Housing> findAllByStandingIdOrderByNameAsc(long standingId);
}
