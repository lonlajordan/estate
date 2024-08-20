package com.estate.repository;

import com.estate.domain.enumaration.Status;
import com.estate.domain.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
  @Transactional
    @Modifying(clearAutomatically = true)
    void deleteAllByGameIdAndStatus(long gameId, Status status);
}
