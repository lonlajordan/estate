package com.estate.domain.entity;

import com.estate.domain.enumaration.Level;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Log extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Level level = Level.INFO;
    private String message = "";
    @Column(columnDefinition = "TEXT")
    private String details = "";

    public Log(Level level, String message) {
        this.level = level;
        this.message = message;
    }

    public Log(Level level, String message, String details) {
        this.level = level;
        this.message = message;
        this.details = details;
    }

    public static Log info(String message){
        return new Log(Level.INFO, message);
    }
    public static Log warn(String message){
        return new Log(Level.WARN, message);
    }
    public static Log error(String message, String details){
        return new Log(Level.ERROR, message, details);
    }
}
