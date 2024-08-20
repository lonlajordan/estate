package com.estate.repository;

import com.estate.domain.enumaration.GameType;
import com.estate.domain.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long>, JpaRepository<Game, Long> {
    long countAllByType(GameType gameType);
    @EntityGraph(attributePaths = {"participations", "winner", "initiator"})
    Page<Game> findAllByTypeInOrderByDateDesc(List<GameType> types, Pageable pageable);
    long countAllByTypeAndDateBetween(GameType type, LocalDateTime start, LocalDateTime end);
    @EntityGraph(attributePaths = {"participations"})
    List<Game> findAllByClosedFalseAndEndingBefore(LocalDateTime date);
}
