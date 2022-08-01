package ru.netology.cardwork.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Getter
@Slf4j
@Validated
public class ConfirmationDto {
    @NotBlank
    private final String OperationId;
    @NotBlank
    private final String VerificationCode;

    public ConfirmationDto(String operationId, String code) {
        this.OperationId = operationId;
        this.VerificationCode = code;
        log.debug("a confirmation object built: {}", this);
    }

    @Override
    public String toString() {
        return '{' + OperationId + ':' + VerificationCode + '}';
    }
}
