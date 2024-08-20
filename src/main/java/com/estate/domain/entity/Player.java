package com.estate.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Player extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName = "";
    private String lastName;
    @Column(nullable = false, unique = true)
    private String pseudo = "";
    @Column(nullable = false)
    private int birthYear;
    private char sex = 'M';
    @ManyToOne
    private Housing city;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String email = "";
    @Column(nullable = false)
    private String otp = "";
    private String token = ""; // token for FCM (Firebase Cloud Messaging)
    @Column(nullable = false)
    private LocalDateTime otpExpiredAt = LocalDateTime.now();
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime mediaSuscriptionDate;
    private LocalDateTime mediaSuscriptionExpirationDate;
    private transient Boolean hasAccessToMedia;
    private String fileName;
    private String picture;
    @Column(unique = true)
    private String userId;
    private String password;
    @Column(nullable = false)
    private Boolean enabled = true;
    @Column(nullable = false)
    private int balance = 0;
    @Enumerated
    private com.estate.domain.enumaration.Locale language = com.estate.domain.enumaration.Locale.FR;
    @Column(nullable = false)
    private Boolean deleted = false;
    @OneToMany(mappedBy = "sender", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Invitation> invitations = new ArrayList<>();
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Invitation> receipts = new ArrayList<>();
    @OneToMany(mappedBy = "player", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Recharge> recharges = new ArrayList<>();
    @OneToMany(mappedBy = "initiator")
    private List<Game> initiated = new ArrayList<>();
    @OneToMany(mappedBy = "winner")
    private List<Game> won = new ArrayList<>();

    public String getName(){
        return Stream.of(firstName, lastName).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

    public int getAge(){
        return Calendar.getInstance().get(Calendar.YEAR) - birthYear;
    }

    public java.util.Locale getLocale(){
        String code = language == null ? "en" : language.name().toLowerCase();
        return new java.util.Locale(code);
    }

    public Boolean getHasAccessToMedia() {
        return mediaSuscriptionExpirationDate != null && LocalDateTime.now().isBefore(mediaSuscriptionExpirationDate);
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.firstName != null) this.firstName = this.firstName.trim().toUpperCase();
        if(this.lastName != null) this.lastName = Arrays.stream(this.lastName.trim().toLowerCase().split("\\s+")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(this.email != null) this.email = this.email.trim();
    }

    @PreRemove
    public void beforeDelete(){
        this.initiated.forEach(game -> game.setInitiator(null));
        this.won.forEach(game -> game.setWinner(null));
    }
}
