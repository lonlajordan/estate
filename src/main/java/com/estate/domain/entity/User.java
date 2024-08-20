package com.estate.domain.entity;

import com.estate.configuration.RoleListConverter;
import com.estate.domain.enumaration.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "champs requis")
    @Column(nullable = false)
    private String firstName = "";
    @NotBlank(message = "champs requis")
    @Column(nullable = false)
    private String lastName = "";
    @Email(message = "adresse e-mail invalide")
    @NotBlank(message = "champs requis")
    @Column(nullable = false, unique = true)
    private String email = "";
    @NotBlank(message = "champs requis")
    @Column(nullable = false)
    private String password = "";
    @NotBlank(message = "champs requis")
    @Column(nullable = false)
    private String phoneNumber = "";
    private String token;
    private LocalDateTime tokenExpireAt = LocalDateTime.now();
    private char sex = 'M';
    private boolean enabled = true;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastLogin;
    @Convert(converter = RoleListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Role> roles = new ArrayList<>();
    private Boolean deleted = Boolean.FALSE;

    public String getName(){
        return Stream.of(firstName, lastName).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.firstName != null) this.firstName = this.firstName.trim().toUpperCase();
        if(this.lastName != null) this.lastName = Arrays.stream(this.lastName.trim().toLowerCase().split("\\s+")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(this.email != null) this.email = this.email.trim();
        if(this.phoneNumber != null) this.phoneNumber = this.phoneNumber.trim();
    }
}
