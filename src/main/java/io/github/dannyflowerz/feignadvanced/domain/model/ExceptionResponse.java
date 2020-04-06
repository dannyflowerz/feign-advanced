package io.github.dannyflowerz.feignadvanced.domain.model;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExceptionResponse {

    public enum ErrorCode {
        UnexpectedError
    }

    private Date timestamp;
    private ErrorCode errorCode;
    private String details;

}
