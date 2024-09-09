package com.estate.domain.service.face;

import com.estate.domain.entity.Notification;
import com.estate.domain.entity.Student;
import com.estate.domain.form.StudentForm;
import com.estate.domain.form.StudentSearch;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.Optional;

public interface StudentService {
    long count();
    Page<Student> findAll(int page);
    Page<Student> findAll(StudentSearch form);
    Optional<Student> findById(Long id);
    Notification save(StudentForm form);
    Notification toggleById(long id);
}
