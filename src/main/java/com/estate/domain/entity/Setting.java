package com.estate.domain.entity;

import com.estate.domain.enumaration.SettingCode;
import com.estate.domain.enumaration.SettingType;
import com.estate.domain.form.SettingForm;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.util.Optional;

@Setter
@Getter
@Entity
public class Setting extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Convert(converter = SettingCode.Converter.class)
    @Enumerated(EnumType.STRING)
    private SettingCode code;
    @Enumerated(EnumType.STRING)
    private SettingType type = SettingType.INTEGER;
    private String value;

    public Setting() {
    }

    public Setting(SettingCode code, SettingType type, String value) {
        this.code = code;
        this.type = type;
        this.value = value;
    }

    public SettingForm toForm(){
        SettingForm form = new SettingForm();
        form.setId(id);
        form.setType(type);
        form.setValue(value);
        form.setName(Optional.ofNullable(code).map(SettingCode::getName).orElse(""));
        return form;
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        this.value = StringUtils.trim(value);
    }
}
