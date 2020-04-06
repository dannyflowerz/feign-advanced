package io.github.dannyflowerz.feignadvanced.service.client;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.dannyflowerz.feignadvanced.domain.exception.ServiceInvocationException;

class HttpStatClientTest {

    private HttpStatClient.HttpStatClientFallback underTest = new HttpStatClient.HttpStatClientFallback();

    @Test
    @DisplayName("SHOULD throw ServiceInvocationException WHEN falling back for get200")
    void get200() {
        // when - then
        assertThrows(ServiceInvocationException.class, () -> underTest.get200(100));
    }

    @Test
    @DisplayName("SHOULD throw ServiceInvocationException WHEN falling back for get400")
    void get400() {
        assertThrows(ServiceInvocationException.class, () -> underTest.get400(100));
    }
}