package com.estate.repository;

import com.estate.domain.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    @Query("SELECT p FROM Player p WHERE p.pseudo = ?1 OR p.email =?1")
    long countAllByCityId(long cityId);
    long countAllByDeletedFalse();
    Page<Player> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countAllByBirthYearGreaterThan(int min);
    long countAllByCityIdAndBirthYearGreaterThan(long cityId, int min);
}
