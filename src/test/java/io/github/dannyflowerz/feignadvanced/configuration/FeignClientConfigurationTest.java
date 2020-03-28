package io.github.dannyflowerz.feignadvanced.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import feign.Client;
import feign.Logger;
import feign.codec.ErrorDecoder;
import feign.hystrix.HystrixFeign;

class FeignClientConfigurationTest {

    private FeignClientConfiguration underTest = new FeignClientConfiguration();

    @Test
    @DisplayName("SHOULD return a HystrixFeign Builder without TLS capabilities WHEN TLS is not enabled")
    void customFeignBuilder() {
        // given
        ReflectionTestUtils.setField(underTest, "withTls", false);

        // when
        HystrixFeign.Builder builder = underTest.customFeignBuilder();

        // then
        Client.Default client = (Client.Default) ReflectionTestUtils.getField(builder, "client");
        assertNotNull(client);
        assertNull(ReflectionTestUtils.getField(client, "sslContextFactory"));
    }

    @Test
    @DisplayName("SHOULD return a HystrixFeign Builder with TLS capabilities WHEN TLS is enabled")
    void customFeignBuilderTls() {
        // given
        ReflectionTestUtils.setField(underTest, "withTls", true);
        ReflectionTestUtils.setField(underTest, "trustStoreResource", "/trust.jks");

        // when
        HystrixFeign.Builder builder = underTest.customFeignBuilder();

        // then
        Client.Default client = (Client.Default) ReflectionTestUtils.getField(builder, "client");
        assertNotNull(client);
        assertNotNull(ReflectionTestUtils.getField(client, "sslContextFactory"));
    }

    @Test
    @DisplayName("SHOULD return a HystrixFeign Builder with MA-TLS capabilities WHEN TLS is enabled")
    void customFeignBuilderMaTls() {
        // given
        ReflectionTestUtils.setField(underTest, "withTls", true);
        ReflectionTestUtils.setField(underTest, "trustStoreResource", "/trust.jks");
        ReflectionTestUtils.setField(underTest, "keyStoreResource", "/testkey.jks");
        ReflectionTestUtils.setField(underTest, "keyStorePassword", "fake");

        // when
        HystrixFeign.Builder builder = underTest.customFeignBuilder();

        // then
        Client.Default client = (Client.Default) ReflectionTestUtils.getField(builder, "client");
        assertNotNull(client);
        assertNotNull(ReflectionTestUtils.getField(client, "sslContextFactory"));
    }

    @Test
    @DisplayName("SHOULD return a CustomFeignLogger instance")
    void customFeignLogger() {
        // given - when
        Logger logger = underTest.customFeignLogger();

        // then
        assertTrue(logger instanceof CustomFeignLogger);
    }

    @Test
    @DisplayName("SHOULD return BASIC Feign log level")
    void customFeignLoggerLevel() {
        // given - when
        Logger.Level level = underTest.customFeignLoggerLevel();

        // then
        assertEquals(Logger.Level.BASIC, level);
    }

    @Test
    @DisplayName("SHOULD return a CustomErrorDecoder instance")
    void customErrorDecoder() {
        // given - when
        ErrorDecoder errorDecoder = underTest.customErrorDecoder();

        // then
        assertTrue(errorDecoder instanceof CustomErrorDecoder);
    }

}