package com.estate.domain.entity;

import com.estate.domain.converter.ModeListConverter;
import com.estate.domain.converter.RoleListConverter;
import com.estate.domain.enumaration.Gender;
import com.estate.domain.enumaration.Mode;
import com.estate.domain.enumaration.Role;
import com.estate.domain.form.Phone;
import com.estate.domain.form.ProfilForm;
import com.estate.domain.form.UserForm;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    @Column(nullable = false)
    private String firstName = "";
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email = "";
    @Column(nullable = false)
    private String password = "";
    @Column(nullable = false)
    private String phone = "";
    private String token;
    private LocalDateTime tokenExpirationDate = LocalDateTime.now();
    @Convert(converter = Gender.Converter.class)
    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.MALE;
    private boolean active = true;
    private LocalDateTime lastLogin;
    @Convert(converter = ModeListConverter.class)
    private List<Mode> modes = new ArrayList<>();
    @Convert(converter = RoleListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy = "validator")
    private List<Payment> payments = new ArrayList<>();

    public String getName(){
        return Stream.of(firstName, lastName).filter(StringUtils::isNotBlank).collect(Collectors.joining(" "));
    }

    @PrePersist
    @PreUpdate
    public void beforeSave(){
        if(this.firstName != null) this.firstName = this.firstName.trim().toUpperCase();
        if(this.lastName != null) this.lastName = Arrays.stream(this.lastName.trim().toLowerCase().split("\\s+")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        if(this.email != null) this.email = this.email.trim();
        if(this.phone != null) this.phone = this.phone.trim();
    }

    public UserForm toForm(){
        UserForm form = new UserForm();
        form.setId(id);
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setGender(gender);
        form.setEmail(email);
        form.setModes(modes);
        form.setRoles(roles);
        form.setPhone(Phone.parse(phone));
        return form;
    }

    public ProfilForm toProfilForm(){
        ProfilForm form = new ProfilForm();
        form.setId(id);
        form.setFirstName(firstName);
        form.setLastName(lastName);
        form.setGender(gender);
        form.setEmail(email);
        form.setPhone(Phone.parse(phone));
        return form;
    }
}
