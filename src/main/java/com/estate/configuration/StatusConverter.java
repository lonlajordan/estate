package com.estate.configuration;

import com.estate.domain.enumaration.Status;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StatusConverter implements Converter<String, Status> {
    @Override
    public Status convert(String value) {
        return Status.values()[Integer.parseInt(value)];
    }
}
