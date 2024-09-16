package com.estate.domain.form;

import com.estate.domain.annotation.FileSize;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Data
public class PartnerForm {
    @NotBlank
    private String name;
    @FileSize(extensions = {"png", "jpg", "jpeg"}, max = 2 * 1024 * 1024)
    private MultipartFile picture;
}
