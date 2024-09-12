package com.estate.domain.form;

import com.estate.domain.annotation.FileSize;
import com.estate.domain.enumaration.Availability;
import com.estate.domain.enumaration.Category;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HousingForm {
    private Long id;
    @NotBlank
    private String name;
    @NotNull
    private Long standingId;
    @NotNull
    private Category category;
    @NotNull
    private Availability status;
    @FileSize(extensions = {"png", "jpg", "jpeg"}, max = 2 * 1024 * 1024)
    private MultipartFile picture;
}
