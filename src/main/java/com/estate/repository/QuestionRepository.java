package com.estate.repository;

import com.estate.domain.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    long countAllByDeletedFalse();
    Page<Question> findAllByOrderByIdDesc(Pageable pageable);
    List<Question> findAllByUploadedFalse();
    List<Question> findAllByIdIn(Set<Long> ids);
}
