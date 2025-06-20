package dev.sleypner.asparser.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.sleypner.asparser.domain.model.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @Valid
    @JsonProperty(value = "captcha")
    private String gRecaptchaResponse;
    @Valid
    @JsonProperty(value = "user")
    private User user;
}
