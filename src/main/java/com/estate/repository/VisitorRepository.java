package com.estate.repository;


import com.estate.domain.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository <Visitor, Long> {
    Optional<Visitor> findByEmail(String email);
    List<Visitor> findVisitorByCreationDateEquals(LocalDateTime localDateTime);

}
