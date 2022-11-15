package ru.netology.cardwork.dto;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * A transient object for accepting a verification code from the front-side.
 */
@Getter
@Slf4j
@Validated
public class ConfirmationDto {
    /**
     * The previously defined identifier of the transaction
     * used to refer to it.
     */
    @NotBlank
    private final String operationId;

    /**
     * The code to confirm the execution of the transfer.
     */
    @NotBlank
    private final String code;

    public ConfirmationDto(String operationId, String code) {
        this.operationId = operationId;
        this.code = code;
        log.trace("a confirmation object built: {}", this);
    }

    @Override
    public String toString() {
        return '{' + operationId + ':' + code + '}';
    }
}
