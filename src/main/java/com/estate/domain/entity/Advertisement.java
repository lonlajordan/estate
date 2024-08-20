package com.estate.domain.entity;

import com.estate.domain.enumaration.MediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Advertisement extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int views = 0;
    @NotBlank(message = "champs requis")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String title = "";
    @Enumerated
    private MediaType type;
    private String fileName = "";
    private String link = "";
    private String externLink;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToMany(cascade = CascadeType.DETACH, mappedBy = "advertisements")
    private List<Game> games = new ArrayList<>();

    @PreRemove
    private void beforeDelete(){
        games.forEach(game -> game.getAdvertisements().remove(this));
    }
}
