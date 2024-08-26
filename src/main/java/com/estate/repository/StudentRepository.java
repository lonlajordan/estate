package com.estate.repository;

import com.estate.domain.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {

    Page<Student> findAllByOrderByCreationDateDesc(Pageable pageable);
    List<Student> findAllByDateOfBirthAndCurrentLeaseNotNull(LocalDate date);
}
