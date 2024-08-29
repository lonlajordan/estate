package com.estate.domain.form;

import com.estate.domain.enumaration.Gender;
import com.estate.domain.enumaration.Mode;
import com.estate.domain.enumaration.Role;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserForm {
    private Long id;
    @NotBlank
    private String firstName;
    private String lastName;
    @NotNull
    private Gender gender;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String phone;
    private List<@NotNull Mode> modes;
    @NotEmpty
    private List<@NotNull Role> roles;
}
