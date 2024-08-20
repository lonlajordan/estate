package com.estate.domain.entity;

import com.estate.domain.enumaration.SettingType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
public class Setting extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String name;
    @Enumerated
    private SettingType type = SettingType.INTEGER;
    private String value;

    public int getValueAsInt(){
        try {
            return Integer.parseInt(value);
        }catch (Exception e){
            return 0;
        }
    }

    public Setting() {
    }

    public Setting value(String value) {
        this.value = value;
        return this;
    }

    public Setting(String code, String name, SettingType type, String value) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.value = value;
    }
}
