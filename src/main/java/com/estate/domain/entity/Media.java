package com.estate.domain.entity;

import com.estate.domain.enumaration.MediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Media extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title = "";
    @Enumerated
    private MediaType type;
    private String fileName = "";
    private String link = "";
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
