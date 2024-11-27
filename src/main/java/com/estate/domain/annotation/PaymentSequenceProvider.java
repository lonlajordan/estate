package com.estate.domain.annotation;

import com.estate.domain.form.PaymentForm;
import com.estate.domain.form.StudentForm;
import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;

import java.util.ArrayList;
import java.util.List;

public class PaymentSequenceProvider implements DefaultGroupSequenceProvider<PaymentForm> {
    @Override
    public List<Class<?>> getValidationGroups(PaymentForm paymentForm) {
        List<Class<?>> sequence = new ArrayList<>();
        sequence.add(StudentForm.class);
        if(paymentForm != null && paymentForm.getId() == null) sequence.add(ConditionalValidation.class);
        return  sequence;
    }
}
