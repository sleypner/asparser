package com.sleypner.parserarticles.model.source.other;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sleypner.parserarticles.model.source.entityes.Users;
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
    private Users user;
}
