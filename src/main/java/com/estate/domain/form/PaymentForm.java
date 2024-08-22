package com.estate.domain.form;

import com.estate.domain.enumaration.Mode;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PaymentForm {
    private Long studentId;
    private Long standingId;
    private Long desiderataId;
    private Integer months;
    private Mode mode;
    private MultipartFile proofFile;
}
