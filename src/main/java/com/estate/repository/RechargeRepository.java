package com.estate.repository;

import com.estate.domain.enumaration.Status;
import com.estate.domain.entity.Recharge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RechargeRepository extends JpaRepository<Recharge, Long> {
    @Query("SELECT COALESCE(SUM(r.amount),0) FROM Recharge r WHERE r.status = ?1")
    long sumAllAmount(Status status);
    @Query("SELECT COALESCE(SUM(r.amount),0) FROM Recharge r WHERE r.player.city.id = ?1 AND r.status = ?2")
    long sumAllAmountByPlayerCityId(long cityId, Status status);
    @Query("SELECT COALESCE(SUM(r.amount),0) FROM Recharge r WHERE r.player.birthYear > ?1 AND r.status = ?2")
    long sumAllAmountByPlayerBirthYearGreaterThan(int min, Status status);
    @Query("SELECT COALESCE(SUM(r.amount),0) FROM Recharge r WHERE r.player.city.id = ?1 AND r.player.birthYear > ?2 AND r.status = ?3")
    long sumAllAmountByPlayerCityIdAndPlayerBirthYearGreaterThan(long cityId, int min, Status status);
    Page<Recharge> findAllByOrderByDateDesc(Pageable pageable);
    @Transactional
    @Modifying(clearAutomatically = true)
    void deleteAllByIdInAndStatus(List<Long> ids, Status status);
}
