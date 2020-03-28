package io.github.dannyflowerz.feignadvanced.domain.exception;

/**
 * This exception is thrown as part of the Hystrix error/fallback mechanism when a service invocation failed
 */
public final class ServiceInvocationException extends RuntimeException {

    /**
     * Handles Hystrix error or fallback scenarios
     *
     * @param message describes the error/fallback situation
     */
    public ServiceInvocationException(String message) {
        super(message);
    }

}
