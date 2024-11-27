package com.estate.domain.annotation;

import com.estate.domain.form.StudentForm;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

public class StudentSequenceProvider implements DefaultGroupSequenceProvider<StudentForm> {
    @Override
    public List<Class<?>> getValidationGroups(StudentForm studentForm) {
        List<Class<?>> sequence = new ArrayList<>();
        sequence.add(StudentForm.class);
        if(studentForm != null && studentForm.getId() == null) sequence.add(ConditionalValidation.class);
        return  sequence;
    }
}
