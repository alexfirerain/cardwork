package ru.netology.cardwork.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@Getter
@Validated
public class ConfirmationDto {
    @NotBlank
    private final String OperationId;
    @NotBlank
    private final String VerificationCode;
}
