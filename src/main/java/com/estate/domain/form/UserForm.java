package com.estate.domain.form;

import com.estate.domain.annotation.FileSize;
import com.estate.domain.enumaration.Gender;
import com.estate.domain.enumaration.Mode;
import com.estate.domain.enumaration.Role;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
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
    @NotNull
    @Valid
    private Phone phone;
    @NotNull
    @Valid
    private Phone mobile;
    private List<@NotNull Mode> modes;
    @NotEmpty
    private List<@NotNull Role> roles;
    @FileSize(extensions = {"png", "jpg", "jpeg"}, max = 2 * 1024 * 1024)
    private MultipartFile picture;
}
