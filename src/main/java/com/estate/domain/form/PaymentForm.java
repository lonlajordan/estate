package com.estate.domain.form;

import com.estate.domain.enumaration.Mode;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PaymentForm {
    private Long id;
    private Long studentId;
    private Long standingId;
    private Long desiderataId;
    private Integer rent;
    private Integer months;
    private Integer caution;
    private Integer repair;
    private Mode mode;
    private MultipartFile proofFile;

    public Integer getAmount(){
        return rent * months + caution + repair;
    }
}
