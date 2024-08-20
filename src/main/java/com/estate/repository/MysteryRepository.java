package com.estate.repository;

import com.estate.domain.entity.Mystery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MysteryRepository extends JpaRepository<Mystery, Long> {
    long countAllByDeletedFalse();
    Page<Mystery> findAllByDeletedFalseOrderByWeekDesc(Pageable pageable);
}
