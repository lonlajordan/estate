package com.estate.domain.form;

import com.estate.domain.annotation.FileSize;
import com.estate.domain.enumaration.Mode;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Data
public class PaymentForm {
    private String id;
    @NotBlank
    private String studentId;
    @NotBlank
    private String standingId;
    @NotBlank
    private String desiderataId;
    @Min(12)
    private Integer months;
    @Min(1)
    private Integer rent;
    @PositiveOrZero
    private Integer caution;
    @PositiveOrZero
    private Integer repair;
    @NotNull
    private Mode mode;
    @FileSize(extensions = {"pdf", "png", "jpg", "jpeg"}, max = 2 * 1024 * 1024)
    private MultipartFile proofFile;

    public Integer getAmount(){
        return rent * months + caution + repair;
    }
}
