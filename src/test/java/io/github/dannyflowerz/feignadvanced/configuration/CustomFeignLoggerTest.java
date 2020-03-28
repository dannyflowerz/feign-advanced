package io.github.dannyflowerz.feignadvanced.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;

class CustomFeignLoggerTest {

    private static final Integer STATUS_200 = 200;
    private static final Integer STATUS_400 = 400;
    private static final Integer ELAPSED_TIME = 381;
    private static final String TEST_BODY = "testbody";

    private Logger myLogger;
    private ListAppender<ILoggingEvent> testLoggerAppender;
    private String url = "http://consumable-service/consumable-resource";
    private Request request;
    private CustomFeignLogger underTest = new CustomFeignLogger();

    @BeforeEach
    void setUp() {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        myLogger = context.getLogger(CustomFeignLogger.class);
        myLogger.setLevel(Level.INFO);
        testLoggerAppender = new ListAppender<>();
        testLoggerAppender.start();
        myLogger.addAppender(testLoggerAppender);
        request = Request.create(Request.HttpMethod.GET, url, Collections.emptyMap(), Request.Body.empty(), new RequestTemplate());
    }

    @Test
    @DisplayName("SHOULD not log anything WHEN invoking the log method")
    void log() {
        // given - when
        underTest.log("test", "test", new Object());

        // then
        assertTrue(testLoggerAppender.list.isEmpty());
    }

    @Test
    @DisplayName("SHOULD log the request method and URL WHEN SLF4J log level is higher than DEBUG")
    void logRequest() {
        // given - when
        underTest.logRequest("test", feign.Logger.Level.BASIC, request);

        // then
        assertEquals(1, testLoggerAppender.list.size());
        assertEquals(Level.INFO, testLoggerAppender.list.get(0).getLevel());
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(Request.HttpMethod.GET.name()));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(url));
        assertFalse(testLoggerAppender.list.get(0).getFormattedMessage().contains("Headers"));
    }

    @Test
    @DisplayName("SHOULD log the request method, URL and headers WHEN SLF4J log level is DEBUG")
    void logRequestDebug() {
        // given
        myLogger.setLevel(Level.DEBUG);

        // when
        underTest.logRequest("test", feign.Logger.Level.BASIC, request);

        // then
        assertEquals(1, testLoggerAppender.list.size());
        assertEquals(Level.DEBUG, testLoggerAppender.list.get(0).getLevel());
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(Request.HttpMethod.GET.name()));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(url));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains("Headers"));
    }

    @Test
    @DisplayName("SHOULD log the response status WHEN it is not an error response and  SLF4J log level is higher than DEBUG")
    void logAndRebufferResponse() throws IOException {
        // given
        Response response = Response.builder().body(TEST_BODY, Charset.defaultCharset()).status(STATUS_200).request(request).build();

        // when
        underTest.logAndRebufferResponse("test", feign.Logger.Level.BASIC, response, ELAPSED_TIME);

        // then
        assertEquals(1, testLoggerAppender.list.size());
        assertEquals(Level.INFO, testLoggerAppender.list.get(0).getLevel());
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(ELAPSED_TIME.toString().concat("ms")));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(STATUS_200.toString()));
        assertFalse(testLoggerAppender.list.get(0).getFormattedMessage().contains("Headers"));
        assertFalse(testLoggerAppender.list.get(0).getFormattedMessage().contains(TEST_BODY));
    }

    @Test
    @DisplayName("SHOULD log the response status and headers WHEN it is not an error response and  SLF4J log level is DEBUG")
    void logAndRebufferResponseDebug() throws IOException {
        // given
        Response response = Response.builder().body(TEST_BODY, Charset.defaultCharset()).status(STATUS_200).request(request).build();
        myLogger.setLevel(Level.DEBUG);

        // when
        underTest.logAndRebufferResponse("test", feign.Logger.Level.BASIC, response, ELAPSED_TIME);

        // then
        assertEquals(1, testLoggerAppender.list.size());
        assertEquals(Level.DEBUG, testLoggerAppender.list.get(0).getLevel());
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(ELAPSED_TIME.toString().concat("ms")));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(STATUS_200.toString()));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains("Headers"));
        assertFalse(testLoggerAppender.list.get(0).getFormattedMessage().contains(TEST_BODY));
    }

    @Test
    @DisplayName("SHOULD log the response status and body WHEN it is an error response and  SLF4J log level is higher than DEBUG")
    void logAndRebufferResponseError() throws IOException {
        // given
        Response response = Response.builder().body(TEST_BODY, Charset.defaultCharset()).status(STATUS_400).request(request).build();

        // when
        underTest.logAndRebufferResponse("test", feign.Logger.Level.BASIC, response, ELAPSED_TIME);

        // then
        assertEquals(1, testLoggerAppender.list.size());
        assertEquals(Level.INFO, testLoggerAppender.list.get(0).getLevel());
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(ELAPSED_TIME.toString().concat("ms")));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(STATUS_400.toString()));
        assertFalse(testLoggerAppender.list.get(0).getFormattedMessage().contains("Headers"));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(TEST_BODY));
    }

    @Test
    @DisplayName("SHOULD log the response status, headers, and body WHEN it is an error response and  SLF4J log level is DEBUG")
    void logAndRebufferResponseErrorDebug() throws IOException {
        // given
        Response response = Response.builder().body(TEST_BODY, Charset.defaultCharset()).status(STATUS_400).request(request).build();
        myLogger.setLevel(Level.DEBUG);

        // when
        underTest.logAndRebufferResponse("test", feign.Logger.Level.BASIC, response, ELAPSED_TIME);

        // then
        assertEquals(1, testLoggerAppender.list.size());
        assertEquals(Level.DEBUG, testLoggerAppender.list.get(0).getLevel());
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(ELAPSED_TIME.toString().concat("ms")));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(STATUS_400.toString()));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains("Headers"));
        assertTrue(testLoggerAppender.list.get(0).getFormattedMessage().contains(TEST_BODY));
    }

    @Test
    @DisplayName("SHOULD throw a NPE WHEN the response status is unknown")
    void logAndRebufferResponseNpe() {
        // given
        Response response = Response.builder().body(TEST_BODY, Charset.defaultCharset()).status(999).request(request).build();

        // when - then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> underTest.logAndRebufferResponse("test", feign.Logger.Level.BASIC, response, 381));
        assertEquals("Unable to interpret response status", exception.getMessage());
    }

}