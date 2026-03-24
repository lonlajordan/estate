package com.estate.domain.entity;

import com.estate.domain.enumaration.SettingCode;
import com.estate.domain.form.SettingForm;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Getter
@Entity
public class Setting extends Auditable {
    @Id
    @UuidGenerator
    private String id;
    @Column(nullable = false)
    @Convert(converter = SettingCode.Converter.class)
    private SettingCode code;
    private String value;

    public Setting() {
    }

    public Setting(SettingCode code, String value) {
        this.code = code;
        this.value = value;
    }

    public SettingForm toForm(){
        SettingForm form = new SettingForm();
        form.setId(id);
        form.setCode(code);
        form.setValue(value);
        return form;
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        this.value = StringUtils.trim(value);
    }
}
