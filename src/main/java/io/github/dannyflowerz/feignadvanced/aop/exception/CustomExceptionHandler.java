package io.github.dannyflowerz.feignadvanced.aop.exception;

import java.util.Date;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.netflix.hystrix.exception.HystrixRuntimeException;

import io.github.dannyflowerz.feignadvanced.domain.exception.ServiceInvocationException;
import io.github.dannyflowerz.feignadvanced.domain.model.ExceptionResponse;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(HystrixRuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleHystrixRuntimeException(HystrixRuntimeException ex) {
        ExceptionResponse exceptionResponse = ExceptionResponse.builder()
                                                               .timestamp(new Date())
                                                               .errorCode(ExceptionResponse.ErrorCode.UnexpectedError)
                                                               .details(extractOwnException(ex).getMessage())
                                                               .build();
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    private ServiceInvocationException extractOwnException(HystrixRuntimeException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof ServiceInvocationException) {
            return (ServiceInvocationException) cause;
        }
        if (ex.getFallbackException() != null && ex.getFallbackException().getCause() != null && ex.getFallbackException().getCause().getCause() instanceof ServiceInvocationException) {
            cause = ex.getFallbackException().getCause().getCause();
            return (ServiceInvocationException) cause;
        }
        return buildDefaultException();
    }

    private ServiceInvocationException buildDefaultException() {
        return new ServiceInvocationException("An unexpected error occurred in a Feign client");
    }

}
