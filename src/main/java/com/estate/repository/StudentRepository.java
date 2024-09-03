package com.estate.repository;

import com.estate.domain.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Page<Student> findAllByOrderByCreationDateDesc(Pageable pageable);
    List<Student> findAllByDateOfBirthAndCurrentLeaseNotNull(LocalDate date);
    @Query("SELECT s FROM Student s WHERE s.currentLease.endDate < ?1 AND s.currentLease.nextLease IS NOT NULL")
    List<Student> findAllByHavingPendingLease(LocalDate date);
}
