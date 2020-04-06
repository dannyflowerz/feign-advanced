package io.github.dannyflowerz.feignadvanced.aop.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.netflix.hystrix.exception.HystrixRuntimeException;

import io.github.dannyflowerz.feignadvanced.domain.exception.ServiceInvocationException;
import io.github.dannyflowerz.feignadvanced.domain.model.ExceptionResponse;

class CustomExceptionHandlerTest {

    private CustomExceptionHandler underTest = new CustomExceptionHandler();
    private String message = "Testing exception handler behaviour";

    @Test
    @DisplayName("SHOULD return default message WHEN receiving a HystrixRuntimeException with an unexpected structure")
    void handleHystrixRuntimeException_Unexpected() {
        // given
        Date date = new Date();
        HystrixRuntimeException testException = new HystrixRuntimeException(null, null, "test", new NullPointerException(message), null);

        // when
        ExceptionResponse exceptionResponse = underTest.handleHystrixRuntimeException(testException).getBody();

        // then
        assertEquals("An unexpected error occurred in a Feign client", exceptionResponse.getDetails());
        assertTrue(exceptionResponse.getTimestamp().compareTo(date) >= 0);
        assertEquals(ExceptionResponse.ErrorCode.UnexpectedError, exceptionResponse.getErrorCode());
    }

    @Test
    @DisplayName("SHOULD extract message from cause member WHEN receiving a HystrixRuntimeException caused by an error status in the HTTP response")
    void handleHystrixRuntimeException_Status() {
        // given
        Date date = new Date();
        HystrixRuntimeException testException = new HystrixRuntimeException(null, null, "test", new ServiceInvocationException(message), null);

        // when
        ExceptionResponse exceptionResponse = underTest.handleHystrixRuntimeException(testException).getBody();

        // then
        assertEquals(message, exceptionResponse.getDetails());
        assertTrue(exceptionResponse.getTimestamp().compareTo(date) >= 0);
        assertEquals(ExceptionResponse.ErrorCode.UnexpectedError, exceptionResponse.getErrorCode());
    }

    @Test
    @DisplayName("SHOULD extract message from fallback member WHEN receiving a HystrixRuntimeException caused by request timeout")
    void handleHystrixRuntimeException_Timeout() {
        // given
        Date date = new Date();
        HystrixRuntimeException testException = new HystrixRuntimeException(null, null, "test", null, new Exception(new Exception(new ServiceInvocationException(message))));

        // when
        ExceptionResponse exceptionResponse = underTest.handleHystrixRuntimeException(testException).getBody();

        // then
        assertEquals(message, exceptionResponse.getDetails());
        assertTrue(exceptionResponse.getTimestamp().compareTo(date) >= 0);
        assertEquals(ExceptionResponse.ErrorCode.UnexpectedError, exceptionResponse.getErrorCode());
    }

}