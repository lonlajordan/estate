package com.estate.domain.entity;

import com.estate.domain.enumaration.Operator;
import com.estate.domain.enumaration.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Recharge extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int amount;
    @ManyToOne(optional = false)
    private Player player;
    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    private String merchantCode;
    private String merchantReference;
    private String clientNumber;
    private String token;
    private String service = "REST";
    private String message;
    @Enumerated
    private Operator operator = Operator.MC;
    private Status status = Status.PENDING;
    private Boolean callback = Boolean.FALSE;
    private String otp;

    public MultiValueMap<String, String> toInitiationForm() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code_marchand", merchantCode);
        formData.add("montant", String.valueOf(amount));
        formData.add("reference_marchand", merchantReference);
        formData.add("numero_client", clientNumber);
        formData.add("token", token);
        formData.add("action", "1");
        formData.add("service", service);
        formData.add("operateur", operator.name());
        return formData;
    }

    public MultiValueMap<String, String> toStatusForm() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code_marchand", merchantCode);
        formData.add("reference_marchand", merchantReference);
        formData.add("token", token);
        formData.add("action", "3");
        formData.add("service", service);
        formData.add("operateur", operator.name());
        return formData;
    }

    public MultiValueMap<String, String> toConfirmationForm() {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code_marchand", merchantCode);
        formData.add("montant", String.valueOf(amount));
        formData.add("token", token);
        formData.add("action", "7");
        formData.add("service", service);
        formData.add("operateur", operator.name());
        formData.add("otp", otp);
        formData.add("numero_client", clientNumber);
        return formData;
    }
}
